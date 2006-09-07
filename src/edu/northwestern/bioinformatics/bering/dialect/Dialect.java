package edu.northwestern.bioinformatics.bering.dialect;

import edu.northwestern.bioinformatics.bering.TableDefinition;

import java.util.List;

import org.apache.ddlutils.model.Column;

/**
 * @author Rhett Sutphin
 */
public interface Dialect {
    String getDialectName();

    List<String> createTable(TableDefinition table);
    List<String> dropTable(String table);

    List<String> addColumn(String table, Column column);
    List<String> removeColumn(String table, String column);

    List<String> setDefaultValue(String table, String column, String defaultValue);
}
