package edu.northwestern.bioinformatics.bering;

import groovy.lang.Closure;
import org.apache.ddlutils.model.Column;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.LinkedList;
import java.util.regex.Pattern;

/**
 * @author Moses Hohman
 */
public abstract class Migration {
    public static final String NULLABLE_KEY  = "nullable";
    public static final String LIMIT_KEY     = "limit";
    public static final String PRECISION_KEY = "precision";
    private static final String DEFAULT_VALUE_KEY = "defaultValue";
    public static final String PRIMARY_KEY_KEY = "primaryKey";

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
        TableDefinition definition = new TableDefinition(name);
        addContents.call(definition);
        adapter.createTable(definition);
    }

    protected void renameTable(String tableName, String newName) {
        renameTable(null, tableName, newName);
    }

    protected void renameTable(Map<String, Object> parameters, String name, String newName) {
        boolean hasPrimaryKey = hasPrimaryKey(parameters, true);
        adapter.renameTable(name, newName, hasPrimaryKey);
    }

    protected void dropTable(String name) {
        dropTable(null, name);
    }

    protected void dropTable(Map<String, Object> parameters, String name) {
        boolean hasPrimaryKey = hasPrimaryKey(parameters, true);
        adapter.dropTable(name, hasPrimaryKey);
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

    protected void renameColumn(String tableName, String columnName, String newColumnName) {
        adapter.renameColumn(tableName, columnName, newColumnName);
    }

    protected void setDefaultValue(String tableName, String columnName, String newDefault) {
        adapter.setDefaultValue(tableName, columnName, newDefault);
    }

    protected void setNullable(String tableName, String columnName, boolean nullable) {
        adapter.setNullable(tableName, columnName, nullable);
    }

    protected void insert(String tableName, Map<String, Object> values) {
        this.insert(null, tableName, values);
    }

    protected void insert(Map<String, Object> parameters, String tableName, Map<String, Object> values) {
        List<String> columns = new LinkedList<String>();
        List<Object> toInsert = new LinkedList<Object>();
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            columns.add(entry.getKey());
            toInsert.add(entry.getValue());
        }
        adapter.insert(tableName, columns, toInsert, hasPrimaryKey(parameters, true));
    }

    protected boolean databaseMatches(String substring) {
        return databaseMatches(
            Pattern.compile(substring, Pattern.CASE_INSENSITIVE)
        );
    }

    private boolean databaseMatches(Pattern regex) {
        return regex.matcher(adapter.getDatabaseName()).find();
    }

    /**
     * Execute one or more SQL statements, separated by semicolons.
     * @param sql
     */
    protected void execute(String sql) {
        adapter.execute(sql);
    }

    ////// IMPLEMENTATION METHODS

    // TODO: maybe this should be moved somewhere else
    // visible to collaborators (e.g., TableDefinition)
    static Column createColumn(Map<String, Object> parameters, String columnName, String columnType) {
        Column column = new Column();
        column.setName(columnName);
        column.setTypeCode(getTypeCode(columnType));
        if (parameters != null && !parameters.isEmpty()) {
            if (parameters.containsKey(NULLABLE_KEY)) {
                column.setRequired(!((Boolean) parameters.get(NULLABLE_KEY)));
            }
            if (parameters.containsKey(DEFAULT_VALUE_KEY) && parameters.get(DEFAULT_VALUE_KEY) != null) {
                column.setDefaultValue(String.valueOf(parameters.get(DEFAULT_VALUE_KEY)));
            }
            if (parameters.containsKey(LIMIT_KEY)) {
                column.setSize(String.valueOf(parameters.get(LIMIT_KEY)));
            }
            if (parameters.containsKey(PRECISION_KEY)) {
                column.setScale((Integer) parameters.get(PRECISION_KEY));
            }
            column.setPrimaryKey(hasPrimaryKey(parameters, false));
        }
        return column;
    }

    private static boolean hasPrimaryKey(Map<String, Object> parameters, boolean def) {
        boolean hasPrimaryKey = def;
        if (parameters != null) {
            if (parameters.containsKey(PRIMARY_KEY_KEY)) {
                hasPrimaryKey = (Boolean) parameters.get(PRIMARY_KEY_KEY);
            }
        }
        return hasPrimaryKey;
    }

    private static int getTypeCode(String typeName) {
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
