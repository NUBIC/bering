package edu.northwestern.bioinformatics.bering;

import edu.northwestern.bioinformatics.bering.runtime.Version;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ddlutils.Platform;
import org.apache.ddlutils.PlatformFactory;
import org.apache.ddlutils.platform.CreationParameters;
import org.apache.ddlutils.platform.oracle.Oracle8Builder;
import org.apache.ddlutils.alteration.AddColumnChange;
import org.apache.ddlutils.alteration.RemoveColumnChange;
import org.apache.ddlutils.alteration.TableChange;
import org.apache.ddlutils.dynabean.SqlDynaClass;
import org.apache.ddlutils.model.Column;
import org.apache.ddlutils.model.Database;
import org.apache.ddlutils.model.Table;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.datasource.ConnectionProxy;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.dao.DataAccessException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.sql.Savepoint;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.Iterator;

import com.sun.java_cup.internal.version;

/**
 * @author rsutphin
 */
public class DatabaseAdapter implements Adapter {
    private static final Log log = LogFactory.getLog(DatabaseAdapter.class);

    public static final String VERSION_TABLE_NAME = "bering_version";
    private static final String RELEASE_COLUMN_NAME = "release";
    private static final String MIGRATION_COLUMN_NAME = "migration";

    private static final Table VERSION_TABLE = createNamedTable(VERSION_TABLE_NAME);
    static {
        // TODO: these columns should be non-null
        VERSION_TABLE.addColumn(createColumn(RELEASE_COLUMN_NAME, Types.INTEGER));
        VERSION_TABLE.addColumn(createColumn(MIGRATION_COLUMN_NAME, Types.INTEGER));
    }

    private Platform platform;
    private Connection connection;
    private boolean defaultAutocommit;
    private JdbcTemplate jdbc;
    private SingleConnectionDataSource dataSource;

    public DatabaseAdapter(Connection connection) {
        this.connection = connection;
        this.dataSource = new SingleConnectionDataSource(connection, true);
        this.jdbc = new JdbcTemplate(dataSource);
        this.platform = PlatformFactory.createNewPlatformInstance(dataSource);
    }

    public void close() {
        try {
            dataSource.destroy();
        } catch (SQLException e) {
            // TODO: make specific
            throw new RuntimeException(e);
        }
    }

    public void beginTransaction() {
        jdbc.execute(new ConnectionCallback() {
            public Object doInConnection(Connection connection) throws SQLException, DataAccessException {
                defaultAutocommit = connection.getAutoCommit();
                connection.setAutoCommit(false);
                return null;
            }
        });
    }

    public void commit() {
        jdbc.execute(new ConnectionCallback() {
            public Object doInConnection(Connection connection) throws SQLException, DataAccessException {
                connection.commit();
                connection.setAutoCommit(defaultAutocommit);
                return null;
            }
        });
    }

    public void rollback() {
        jdbc.execute(new ConnectionCallback() {
            public Object doInConnection(Connection connection) throws SQLException, DataAccessException {
                connection.rollback();
                connection.setAutoCommit(defaultAutocommit);
                return null;
            }
        });
    }

    public void createTable(TableDefinition def) {
        Database db = new Database();
        Table table = def.toTable();
        db.addTable(table);
        CreationParameters cp = new CreationParameters();
        cp.addParameter(table, Oracle8Builder.PARAM_SUPPRESS_AUTOINCREMENT_TRIGGER, "true");
        platform.createTables(db, cp, false, false);
    }

    public void dropTable(String name) {
        platform.dropTables(createDatabaseWithSingleTable(createIdedTable(name)), false);
    }

    public void addColumn(String tableName, Column column) {
        Table table = createIdedTable(tableName);
        TableChange addColumn = new AddColumnChange(table, column, null, null);
        platform.changeDatabase(Arrays.asList(addColumn), false);
    }

    public void removeColumn(String tableName, String columnName) {
        Table table = createIdedTable(tableName);
        Column column = new Column();
        column.setName(columnName);
        TableChange removeColumn = new RemoveColumnChange(table, column);
        platform.changeDatabase(Arrays.asList(removeColumn), false);
    }

    private static Column createColumn(String name, int type) {
        Column column = new Column();
        column.setName(name);
        column.setTypeCode(type);
        return column;
    }

    private static Table createNamedTable(String name) {
        Table table = new Table();
        table.setName(name);
        return table;
    }

    private static Table createIdedTable(String name) {
        Table table = createNamedTable(name);
        Column id = createColumn("id", Types.INTEGER);
        id.setPrimaryKey(true);
        id.setAutoIncrement(true);
        table.addColumn(id);
        return table;
    }

    private static Database createDatabaseWithSingleTable(Table table) {
        Database db = new Database();
        db.addTable(table);
        return db;
    }

    public Version loadVersions() {
        Database db = createDatabaseWithSingleTable(VERSION_TABLE);
        Savepoint savepoint = null;
        final Version version = new Version();
        try {
            savepoint = connection.setSavepoint("versiontabledetect");
            jdbc.query("SELECT " + RELEASE_COLUMN_NAME + ", " + MIGRATION_COLUMN_NAME + " FROM " + VERSION_TABLE_NAME,
                (Object[]) null, new ResultSetExtractor() {
                    public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
                        log.debug(VERSION_TABLE_NAME + " table exists; reading current rows");
                        while (rs.next()) {
                            version.updateRelease(
                                rs.getInt(RELEASE_COLUMN_NAME),
                                rs.getInt(MIGRATION_COLUMN_NAME));
                        }
                        return null;
                    }
                }
            );
            return version;
        } catch (Exception e) {
            log.debug("Assuming " + VERSION_TABLE_NAME + " does not exist due to exception", e);
            try {
                if (savepoint != null) connection.rollback(savepoint);
            } catch (SQLException rollbackE) {
                throw new RuntimeException(VERSION_TABLE_NAME + " does not exist and an attempt to create it has failed", rollbackE);
            }
            log.info("Creating " + VERSION_TABLE_NAME + " table");
            platform.createTables(db, false, false);
            return new Version();
        }
    }

    public void updateVersion(Integer release, Integer migration) {
        Database db = createDatabaseWithSingleTable(VERSION_TABLE);
        platform.evaluateBatch("DELETE FROM " + VERSION_TABLE_NAME + " WHERE " + RELEASE_COLUMN_NAME + " = " + release, false);

        if (migration < 1) return;

        DynaBean newVersion;
        try {
            newVersion = SqlDynaClass.newInstance(VERSION_TABLE).newInstance();
        } catch (IllegalAccessException e) {
            // TODO: make specific
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            // TODO: make specific
            throw new RuntimeException(e);
        }
        newVersion.set(RELEASE_COLUMN_NAME, release);
        newVersion.set(MIGRATION_COLUMN_NAME, migration);
        platform.insert(db, newVersion);
    }
}
