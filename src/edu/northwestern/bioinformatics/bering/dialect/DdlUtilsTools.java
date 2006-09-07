package edu.northwestern.bioinformatics.bering.dialect;

import org.apache.ddlutils.model.Database;
import org.apache.ddlutils.model.Table;
import org.apache.ddlutils.model.Column;

import java.sql.Types;

/**
 * @author Rhett Sutphin
 */
public class DdlUtilsTools {
    public static Database createDatabase(Table table) {
        Database db = new Database();
        db.addTable(table);
        return db;
    }

    public static Column createColumn(String name, int type) {
        Column column = createColumn(name);
        column.setTypeCode(type);
        return column;
    }

    public static Column createColumn(String name) {
        Column column = new Column();
        column.setName(name);
        return column;
    }

    public static Table createTable(String name) {
        Table table = new Table();
        table.setName(name);
        return table;
    }

    /**
     * Creates a table containing an ID column matching the default ID column for Bering tables.
     */
    public static Table createIdedTable(String name) {
        Table table = createTable(name);
        Column id = createColumn("id", Types.INTEGER);
        id.setPrimaryKey(true);
        id.setAutoIncrement(true);
        table.addColumn(id);
        return table;
    }

    private DdlUtilsTools() { }
}
