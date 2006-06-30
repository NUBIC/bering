package edu.northwestern.bioinformatics.bering;

import org.apache.ddlutils.Platform;
import org.apache.ddlutils.PlatformFactory;
import org.apache.ddlutils.dynabean.SqlDynaClass;
import org.apache.ddlutils.alteration.AddColumnChange;
import org.apache.ddlutils.alteration.RemoveColumnChange;
import org.apache.ddlutils.alteration.TableChange;
import org.apache.ddlutils.model.Column;
import org.apache.ddlutils.model.Database;
import org.apache.ddlutils.model.Table;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.Iterator;
import java.sql.Types;

import edu.northwestern.bioinformatics.bering.runtime.Version;

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
        VERSION_TABLE.addColumn(createColumn(RELEASE_COLUMN_NAME, Types.INTEGER));
        VERSION_TABLE.addColumn(createColumn(MIGRATION_COLUMN_NAME, Types.INTEGER));
    }

    private Platform platform;

    // TODO: may need to change this to a single connection, or find some other way to
    // handle transactions
    public DatabaseAdapter(DataSource dataSource) {
        this.platform = PlatformFactory.createNewPlatformInstance(dataSource);
    }

    public void createTable(TableDefinition def) {
        Database db = new Database();
        db.addTable(def.toTable());
        platform.createTables(db, false, false);
    }

    public void dropTable(String name) {
        platform.dropTables(createDatabaseWithSingleTable(createNamedTable(name)), false);
    }

    public void addColumn(String tableName, Column column) {
        Table table = createNamedTable(tableName);
        TableChange addColumn = new AddColumnChange(table, column, null, null);
        platform.changeDatabase(Arrays.asList(addColumn), false);
    }

    public void removeColumn(String tableName, String columnName) {
        Table table = createNamedTable(tableName);
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

    private static Database createDatabaseWithSingleTable(Table table) {
        Database db = new Database();
        db.addTable(table);
        return db;
    }

    public Version loadVersions() {
        Database db = createDatabaseWithSingleTable(VERSION_TABLE);
        Iterator<DynaBean> results;
        try {
            results = (Iterator<DynaBean>) platform.query(db,
                "SELECT release, migration FROM " + VERSION_TABLE_NAME,
                new Table[] { VERSION_TABLE });
        } catch (Exception e) {
            log.info("Creating " + VERSION_TABLE_NAME + " table");
            platform.createTables(db, false, false);
            return new Version();
        }

        Version v = new Version();
        log.debug(VERSION_TABLE_NAME + " table exists; reading current rows");
        while (results.hasNext()) {
            DynaBean dynaBean = results.next();
            v.updateRelease(
                (Integer) dynaBean.get(RELEASE_COLUMN_NAME),
                (Integer) dynaBean.get("migration"));
        }
        return v;
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
        newVersion.set("migration", migration);
        platform.insert(db, newVersion);
    }
}
