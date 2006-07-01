package edu.northwestern.bioinformatics.bering;

import groovy.lang.Closure;
import org.apache.ddlutils.model.Column;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Moses Hohman
 */
public abstract class Migration {
    public static final String NULLABLE_KEY  = "nullable";
    public static final String LIMIT_KEY     = "limit";
    public static final String PRECISION_KEY = "precision";

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

    protected Adapter adapter;

    ////// METHODS FOR MIGRATIONS TO OVERRIDE

    public abstract void up();
    public abstract void down() throws IrreversibleMigration;

    ////// METHODS FOR MIGRATIONS TO CALL

    protected void createTable(String name, Closure addContents) {
        TableDefinition definition = new TableDefinition(name, this);
        addContents.call(definition);
        adapter.createTable(definition);
    }

    protected void dropTable(String name) {
        adapter.dropTable(name);
    }

    protected void addColumn(String tableName, String columnName, String columnType) {
        addColumn(null, tableName, columnName, columnType);
    }

    protected void addColumn(Map<String, Object> parameters, String tableName, String columnName, String columnType) {
        adapter.addColumn(tableName, createColumn(parameters, columnName, columnType));
    }

    protected void removeColumn(String tableName, String columnName) {
        adapter.removeColumn(tableName, columnName);
    }

    ////// IMPLEMENTATION METHODS

    // TODO: maybe this should be moved somewhere else
    // visible to collaborators (e.g., TableDefinition)
    Column createColumn(Map<String, Object> parameters, String columnName, String columnType) {
        Column col = new Column();
        col.setName(columnName);
        col.setTypeCode(getTypeCode(columnType));
        if (parameters != null && !parameters.isEmpty()) {
            if (parameters.containsKey(NULLABLE_KEY)) {
                col.setRequired(!((Boolean) parameters.get(NULLABLE_KEY)));
            }
            if (parameters.containsKey(LIMIT_KEY)) {
                col.setSize(String.valueOf(parameters.get(LIMIT_KEY)));
            }
            if (parameters.containsKey(PRECISION_KEY)) {
                col.setScale((Integer) parameters.get(PRECISION_KEY));
            }
        }
        return col;
    }

    private int getTypeCode(String typeName) {
        Integer code = NAMES_TO_JDBC_TYPES.get(typeName);
        if (code == null) {
            throw new IllegalArgumentException("Unknown type: " + typeName);
        }
        return code;
    }

    public final void setAdapter(Adapter adapter) {
        this.adapter = adapter;
    }
}
