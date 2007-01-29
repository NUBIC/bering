package edu.northwestern.bioinformatics.bering.dialect;

import org.apache.ddlutils.model.Column;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.LinkedList;

import edu.northwestern.bioinformatics.bering.SqlUtils;

/**
 * @author Rhett Sutphin
 */
public class PostgreSQL extends Generic {
    @Override
    public List<String> setNullable(String table, String column, boolean nullable) {
        return Arrays.asList(String.format(
            "ALTER TABLE %s ALTER COLUMN %s %s NOT NULL", table, column,
            nullable ? "DROP" : "SET"
        ));
    }

    @Override
    public List<String> renameTable(String table, String newName, boolean hasPrimaryKey) {
        List<String> statements = new ArrayList<String>(2);
        statements.addAll(super.renameTable(table, newName, hasPrimaryKey));
        if (hasPrimaryKey) {
            // this is weird, but it works
            statements.add(String.format(
                "ALTER TABLE %s RENAME TO %s", createIdSequenceName(table), createIdSequenceName(newName)));
        }
        return statements;
    }

    @Override
    public List<String> addColumn(String tableName, Column column) {
        List<String> statements = new LinkedList<String>();

        // DDLUtils freaks out when attempting to add a non-null and/or defaulted column
        // so apply those attributes afterward
        String defaultValue = column.getDefaultValue();
        column.setDefaultValue(null);
        boolean notnull = column.isRequired();
        column.setRequired(false);

        statements.addAll(super.addColumn(tableName, column));
        if (defaultValue != null) {
            statements.addAll(setDefaultValue(tableName, column.getName(), defaultValue));
            statements.add(String.format(
                "UPDATE %s SET %s=%s WHERE %s IS NULL",
                tableName, column.getName(), SqlUtils.sqlLiteral(defaultValue), column.getName()
            ));
        }
        if (notnull) {
            statements.addAll(setNullable(tableName, column.getName(), false));
        }

        return statements;
    }

    /**
     * Creates the sequence name for the given table, just as the PostgreSQL SERIAL declaration would.
     * @param tableName
     * @return the sequence name
     */
    private static String createIdSequenceName(String tableName) {
        return tableName + "_id_seq";
    }
}
