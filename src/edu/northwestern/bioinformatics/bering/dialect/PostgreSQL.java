package edu.northwestern.bioinformatics.bering.dialect;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

/**
 * @author Rhett Sutphin
 */
public class PostgreSQL extends Generic {
    public List<String> setNullable(String table, String column, boolean nullable) {
        return Arrays.asList(String.format(
            "ALTER TABLE %s ALTER COLUMN %s %s NOT NULL", table, column,
            nullable ? "DROP" : "SET"
        ));
    }


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

    /**
     * Creates the sequence name for the given table, just as the PostgreSQL SERIAL declaration would.
     * @param tableName
     * @return the sequence name
     */
    private static String createIdSequenceName(String tableName) {
        return tableName + "_id_seq";
    }
}
