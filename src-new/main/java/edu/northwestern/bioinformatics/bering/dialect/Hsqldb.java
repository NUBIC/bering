package edu.northwestern.bioinformatics.bering.dialect;

import java.util.List;

/**
 * @author Rhett Sutphin
 */
public class Hsqldb extends Generic {
    @Override
    public List<String> renameColumn(String tableName, String columnName, String newColumnName) {
        return singleStatement(
            "ALTER TABLE %s ALTER COLUMN %s RENAME TO %s", tableName, columnName, newColumnName
        );
    }
}
