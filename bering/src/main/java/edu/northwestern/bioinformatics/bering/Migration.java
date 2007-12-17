package edu.northwestern.bioinformatics.bering;

import groovy.lang.Closure;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.LinkedList;
import java.util.regex.Pattern;
import java.net.URI;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import edu.northwestern.bioinformatics.bering.tools.UriTools;

/**
 * @author Moses Hohman
 * @author Rhett Sutphin
 */
public abstract class Migration {
    private static final Log log = LogFactory.getLog(Migration.class);

    public static final String NULLABLE_KEY      = "nullable";
    public static final String LIMIT_KEY         = "limit";
    public static final String PRECISION_KEY     = "precision";
    public static final String SCALE_KEY         = "scale";
    public static final String DEFAULT_VALUE_KEY = "defaultValue";
    public static final String PRIMARY_KEY_KEY   = "primaryKey";
    public static final String REFERENCES_KEY    = "references";

    public static final Map<String, Integer> NAMES_TO_JDBC_TYPES = new HashMap<String, Integer>();
    static {
        NAMES_TO_JDBC_TYPES.put("string",    Types.VARCHAR);
        NAMES_TO_JDBC_TYPES.put("integer",   Types.INTEGER);
        NAMES_TO_JDBC_TYPES.put("float",     Types.FLOAT);
        NAMES_TO_JDBC_TYPES.put("numeric",   Types.NUMERIC);
        NAMES_TO_JDBC_TYPES.put("boolean",   Types.BIT);
        NAMES_TO_JDBC_TYPES.put("date",      Types.DATE);
        NAMES_TO_JDBC_TYPES.put("time",      Types.TIME);
        NAMES_TO_JDBC_TYPES.put("timestamp", Types.TIMESTAMP);
    }

    protected Adapter adapter;
    protected URI sourceUri;

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
        adapter.addColumn(tableName, Column.createColumn(parameters, columnName, columnType));
    }

    static boolean hasPrimaryKey(Map<String, Object> parameters, boolean def) {
        boolean hasPrimaryKey = def;
        if (parameters != null) {
            if (parameters.containsKey(PRIMARY_KEY_KEY)) {
                hasPrimaryKey = (Boolean) parameters.get(PRIMARY_KEY_KEY);
            }
        }
        return hasPrimaryKey;
    }

    /**
     * Use {@link #dropColumn} instead.
     */
    @Deprecated
    protected void removeColumn(String tableName, String columnName) {
        dropColumn(tableName, columnName);
    }

    protected void dropColumn(String tableName, String columnName) {
        adapter.dropColumn(tableName, columnName);
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

    /**
     * Execute the contents of an external SQL script.  The path will be resolved relative to
     * the current script.
     *
     * @param relativePath
     */
    protected void external(String relativePath) {
        if (sourceUri == null) {
            throw new MigrationExecutionException(
                "Cannot resolve external resource; source URI not set. " +
                "(You probably need to pick a different migration finder.)");
        }

        URL externalUrl = null;
        try {
            if (log.isDebugEnabled()) log.debug("Attempting to resolve " + relativePath + " against " + sourceUri.toString());
            URI resolvedUri = UriTools.resolve(sourceUri, relativePath);
            if (log.isDebugEnabled()) log.debug("Resolved as " + resolvedUri.toString());
            externalUrl = resolvedUri.toURL();
            if (log.isDebugEnabled()) log.debug("Loading external SQL from " + externalUrl.toString());
            String script = IOUtils.toString(externalUrl.openStream());
            execute(script);
        } catch (MalformedURLException e) {
            throw new MigrationExecutionException("Resolving " + relativePath + " against "
                + sourceUri + " failed", e);
        } catch (IOException e) {
            throw new MigrationExecutionException("Reading " + externalUrl + " failed", e);
        }
    }

    ////// IMPLEMENTATION METHODS

    public final void setAdapter(Adapter adapter) {
        this.adapter = adapter;
    }

    public final void setSourceUri(URI sourceUri) {
        this.sourceUri = sourceUri;
    }
}
