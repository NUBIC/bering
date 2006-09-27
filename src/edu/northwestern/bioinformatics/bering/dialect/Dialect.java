package edu.northwestern.bioinformatics.bering.dialect;

import edu.northwestern.bioinformatics.bering.TableDefinition;

import java.util.List;

import org.apache.ddlutils.model.Column;

/**
 * @author Rhett Sutphin
 */
public interface Dialect {
    String getDialectName();

    List<String> separateStatements(String script);

    List<String> createTable(TableDefinition table);
    List<String> dropTable(String table, boolean hasPrimaryKey);

    List<String> addColumn(String table, Column column);
    List<String> removeColumn(String table, String column);
    List<String> renameColumn(String tableName, String columnName, String newColumnName);

    List<String> setDefaultValue(String table, String column, String defaultValue);
    List<String> setNullable(String table, String column, boolean nullable);

    List<String> insert(String table, List<String> columns, List<Object> values, boolean automaticPrimaryKey);
}
