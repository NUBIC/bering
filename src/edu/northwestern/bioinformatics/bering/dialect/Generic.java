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

    public List<String> createTable(TableDefinition def) {
        return createTable(def.toTable());
    }

    // Uses DdlUtils' tokenizer, which just splits on ;
    public List<String> separateStatements(String script) {
        SqlTokenizer tok = new SqlTokenizer(script);
        List<String> stmts = new LinkedList<String>();
        while (tok.hasMoreStatements()) {
            stmts.add(tok.getNextStatement());
        }
        return stmts;
    }

    protected List<String> createTable(Table table) {
        return separateStatements(
            getPlatform().getCreateTablesSql(createDatabase(table), false, false)
        );
    }

    public List<String> dropTable(String tableName) {
        return dropTable(createIdedTable(tableName));
    }

    protected List<String> dropTable(Table table) {
        return separateStatements(
            getPlatform().getDropTablesSql(createDatabase(table), false)
        );
    }

    public List<String> addColumn(String tableName, Column column) {
        Table table = DdlUtilsTools.createTable(tableName);
        AddColumnChange addColumn = new AddColumnChange(table, column, null, null);
        addColumn.setAtEnd(true);
        return getSqlForChanges(addColumn);
    }

    public List<String> removeColumn(String tableName, String columnName) {
        Table table = createIdedTable(tableName);
        Column column = createColumn(columnName);
        TableChange removeColumn = new RemoveColumnChange(table, column);
        return getSqlForChanges(removeColumn);
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

    public List<String> insert(String table, List<String> columns, List<Object> values) {
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

    private List<String> getSqlForChanges(ModelChange... changes) {
        return separateStatements(getPlatform().getChangeDatabaseSql(Arrays.asList(changes)));
    }

    protected List<String> singleStatement(String sql, Object... formatArguments) {
        return Arrays.asList(String.format(sql, formatArguments));
    }

}
