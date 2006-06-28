package edu.northwestern.bioinformatics.bering;

import org.apache.ddlutils.Platform;
import org.apache.ddlutils.PlatformFactory;
import org.apache.ddlutils.alteration.AddColumnChange;
import org.apache.ddlutils.alteration.RemoveColumnChange;
import org.apache.ddlutils.alteration.TableChange;
import org.apache.ddlutils.model.Column;
import org.apache.ddlutils.model.Database;
import org.apache.ddlutils.model.Table;

import javax.sql.DataSource;
import java.util.Arrays;

/**
 * @author rsutphin
 */
public class DatabaseAdapter implements Adapter {
    private Platform platform;

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

    private Table createNamedTable(String name) {
        Table table = new Table();
        table.setName(name);
        return table;
    }

    private Database createDatabaseWithSingleTable(Table table) {
        Database db = new Database();
        db.addTable(table);
        return db;
    }
}
