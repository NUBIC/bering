package edu.northwestern.bioinformatics.bering.dialect;

import static edu.northwestern.bioinformatics.bering.SqlUtils.sqlLiteral;
import org.apache.ddlutils.model.Table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Rhett Sutphin
 */
public class Oracle extends Generic {
    public List<String> createTable(Table table) {
        List<String> statments = new ArrayList<String>(2);
        statments.add("CREATE SEQUENCE " + createIdSequenceName(table));
        statments.addAll(super.createTable(massageTableForOracle(table)));
        return statments;
    }

    public List<String> dropTable(Table table) {
        List<String> statments = new ArrayList<String>(2);
        statments.addAll(super.dropTable(massageTableForOracle(table)));
        statments.add("DROP SEQUENCE " + createIdSequenceName(table));
        return statments;
    }

    public List<String> setDefaultValue(String table, String column, String newDefault) {
        return Arrays.asList(
            "ALTER TABLE " + table + " MODIFY (" + column + " DEFAULT " + sqlLiteral(newDefault) + ')'
        );
    }

    private String createIdSequenceName(Table table) {
        int maxlen = getPlatform().getPlatformInfo().getMaxIdentifierLength();
        return "seq_" + truncate(table.getName(), maxlen - 7) + "_id";
    }

    private String truncate(String str, int maxlen) {
        if (str.length() <= maxlen) return str;
        return str.substring(0, maxlen);
    }

    private Table massageTableForOracle(Table table) {
        table.getPrimaryKeyColumns()[0].setAutoIncrement(false);
        return table;
    }
}
