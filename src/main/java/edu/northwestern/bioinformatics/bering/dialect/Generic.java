package edu.northwestern.bioinformatics.bering.dialect;

import edu.northwestern.bioinformatics.bering.SqlUtils;
import edu.northwestern.bioinformatics.bering.TableDefinition;
import static edu.northwestern.bioinformatics.bering.dialect.DdlUtilsTools.*;
import org.apache.ddlutils.alteration.AddColumnChange;
import org.apache.ddlutils.alteration.ModelChange;
import org.apache.ddlutils.alteration.RemoveColumnChange;
import org.apache.ddlutils.alteration.TableChange;
import org.apache.ddlutils.model.Column;
import org.apache.ddlutils.model.Table;
import org.apache.ddlutils.util.SqlTokenizer;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;

/**
 * @author Rhett Sutphin
 */
public class Generic extends DdlUtilsBasedDialect {
    protected static final String INSERT_DELIMITER = ", ";

    public List<String> renameTable(String table, String newName, boolean hasPrimaryKey) {
        return singleStatement("ALTER TABLE %s RENAME TO %s", table, newName);
    }

    public List<String> renameColumn(String tableName, String columnName, String newColumnName) {
        return Arrays.asList(String.format(
            "ALTER TABLE %s RENAME COLUMN %s TO %s", tableName, columnName, newColumnName
        ));
    }

    public List<String> setDefaultValue(String table, String column, String newDefault) {
        // DDLUtils insists on dropping and recreating the table.  So:
        return Arrays.asList(String.format(
            "ALTER TABLE %s ALTER COLUMN %s SET DEFAULT %s", table, column, SqlUtils.sqlLiteral(newDefault)
        ));
    }

    public List<String> setNullable(String table, String column, boolean nullable) {
        return Arrays.asList(String.format(
            "ALTER TABLE %s ALTER COLUMN %s SET %sNULL", table, column, nullable ? "" : "NOT "
        ));
    }

    public List<String> insert(String table, List<String> columns, List<Object> values, boolean hasPrimaryKey) {
        return Arrays.asList(String.format(
            "INSERT INTO %s (%s) VALUES (%s)",
                table,
                StringUtils.join(columns.iterator(), INSERT_DELIMITER),
                createInsertValueString(values)
        ));
    }

    protected String createInsertValueString(List<Object> values) {
        List<String> valueLiterals = new ArrayList<String>();
        for (Object o : values) {
            valueLiterals.add(SqlUtils.sqlLiteral(o));
        }
        return StringUtils.join(valueLiterals.iterator(), INSERT_DELIMITER);
    }

    ////// UTILITIES

    protected List<String> singleStatement(String sql, Object... formatArguments) {
        return Arrays.asList(String.format(sql, formatArguments));
    }

}
