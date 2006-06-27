package edu.northwestern.bioinformatics.bering;

import org.apache.ddlutils.Platform;
import org.apache.ddlutils.PlatformFactory;
import org.apache.ddlutils.alteration.AddColumnChange;
import org.apache.ddlutils.alteration.TableChange;
import org.apache.ddlutils.alteration.RemoveColumnChange;
import org.apache.ddlutils.model.Column;
import org.apache.ddlutils.model.Database;
import org.apache.ddlutils.model.Table;

import javax.sql.DataSource;
import java.sql.Types;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author rsutphin
 */
public class DatabaseAdapter implements Adapter {
    private static final Map<String, Integer> NAMES_TO_JDBC_TYPES = new HashMap<String, Integer>();
    static {
        NAMES_TO_JDBC_TYPES.put("string",    Types.VARCHAR);
        NAMES_TO_JDBC_TYPES.put("integer",   Types.INTEGER);
        NAMES_TO_JDBC_TYPES.put("float",     Types.NUMERIC);
        NAMES_TO_JDBC_TYPES.put("boolean",   Types.BOOLEAN);
        NAMES_TO_JDBC_TYPES.put("date",      Types.DATE);
        NAMES_TO_JDBC_TYPES.put("time",      Types.TIME);
        NAMES_TO_JDBC_TYPES.put("timestamp", Types.TIMESTAMP);
    }

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

    public int getTypeCode(String typeName) {
        Integer code = NAMES_TO_JDBC_TYPES.get(typeName);
        if (code == null) {
            throw new IllegalArgumentException("Unknown type: " + typeName);
        }
        return code;
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
