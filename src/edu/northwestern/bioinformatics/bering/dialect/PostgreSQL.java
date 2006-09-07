package edu.northwestern.bioinformatics.bering.dialect;

import java.util.List;
import java.util.Arrays;

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
}
