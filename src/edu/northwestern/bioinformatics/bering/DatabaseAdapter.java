package edu.northwestern.bioinformatics.bering;

import edu.northwestern.bioinformatics.bering.dialect.DdlUtilsBasedDialect;
import edu.northwestern.bioinformatics.bering.dialect.Dialect;
import edu.northwestern.bioinformatics.bering.runtime.Version;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ddlutils.Platform;
import org.apache.ddlutils.PlatformFactory;
import org.apache.ddlutils.model.Column;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.StatementCallback;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.List;
import java.util.Collections;

/**
 * @author rsutphin
 */
public class DatabaseAdapter implements Adapter {
    private static final Log log = LogFactory.getLog(DatabaseAdapter.class);

    public static final String VERSION_TABLE_NAME = "bering_version";
    private static final String RELEASE_COLUMN_NAME = "release";
    private static final String MIGRATION_COLUMN_NAME = "migration";

    private static final TableDefinition VERSION_TABLE = new TableDefinition(VERSION_TABLE_NAME);
    static {
        // TODO: these columns should be non-null
        VERSION_TABLE.addColumn(RELEASE_COLUMN_NAME, "integer");
        VERSION_TABLE.addColumn(MIGRATION_COLUMN_NAME, "integer");
        VERSION_TABLE.setIncludePrimaryKey(false);
    }

    private Connection connection;
    private boolean defaultAutocommit;
    private JdbcTemplate jdbc;
    private SingleConnectionDataSource dataSource;
    private Dialect dialect;

    public DatabaseAdapter(Connection connection, Dialect dialect) {
        this.connection = connection;
        this.dataSource = new SingleConnectionDataSource(connection, true);
        this.jdbc = new JdbcTemplate(dataSource);
        this.dialect = dialect;
        if (this.dialect instanceof DdlUtilsBasedDialect) {
            Platform platform = PlatformFactory.createNewPlatformInstance(dataSource);
            ((DdlUtilsBasedDialect) this.dialect).setPlatform(platform);
        }
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
        execute(dialect.createTable(def));
    }

    public void dropTable(String name) {
        execute(dialect.dropTable(name));
    }

    public void addColumn(String tableName, Column column) {
        execute(dialect.addColumn(tableName, column));
    }

    public void removeColumn(String tableName, String columnName) {
        execute(dialect.removeColumn(tableName, columnName));
    }

    public void setDefaultValue(String tableName, String columnName, String newDefault) {
        execute(dialect.setDefaultValue(tableName, columnName, newDefault));
    }

    public void setNullable(String tableName, String columnName, boolean nullable) {
        execute(dialect.setNullable(tableName, columnName, nullable));
    }

    public void execute(String sql) {
        execute(dialect.separateStatements(sql));
    }

    private void execute(final List<String> statements) {
        jdbc.execute(new StatementCallback() {
            public Object doInStatement(Statement stmt) throws SQLException, DataAccessException {
                for (String sql : statements) {
                    if (sql.trim().length() == 0) continue;
                    log.info("SQL: " + sql.replaceAll("\n", "\n     ") + ';');
                    stmt.execute(sql);
                }
                return null;
            }
        });
    }

    public String getDatabaseName() {
        return dialect.getDialectName();
    }

    public Version loadVersions() {
        Savepoint savepoint = null;
        final Version version = new Version();
        try {
            savepoint = connection.setSavepoint("versiontabledetect");
            jdbc.query(String.format(
                "SELECT %s, %s FROM %s", RELEASE_COLUMN_NAME, MIGRATION_COLUMN_NAME, VERSION_TABLE_NAME),
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
            createTable(VERSION_TABLE);
            return new Version();
        }
    }

    public void updateVersion(Integer release, Integer migration) {
        execute(String.format("DELETE FROM %s WHERE %s = %d",
            VERSION_TABLE_NAME, RELEASE_COLUMN_NAME, release));

        if (migration < 1) return;

        execute(String.format("INSERT INTO %s (%s, %s) VALUES (%d, %d)",
            VERSION_TABLE_NAME, RELEASE_COLUMN_NAME, MIGRATION_COLUMN_NAME, release, migration));
    }
}
