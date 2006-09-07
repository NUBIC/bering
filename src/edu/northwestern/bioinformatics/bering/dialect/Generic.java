package edu.northwestern.bioinformatics.bering.dialect;

import static edu.northwestern.bioinformatics.bering.SqlUtils.*;
import edu.northwestern.bioinformatics.bering.TableDefinition;
import static edu.northwestern.bioinformatics.bering.dialect.DdlUtilsTools.*;
import org.apache.ddlutils.model.Table;
import org.apache.ddlutils.model.Column;
import org.apache.ddlutils.alteration.ModelChange;
import org.apache.ddlutils.alteration.AddColumnChange;
import org.apache.ddlutils.alteration.TableChange;
import org.apache.ddlutils.alteration.RemoveColumnChange;

import java.util.List;
import java.util.Arrays;

/**
 * @author Rhett Sutphin
 */
public class Generic extends DdlUtilsBasedDialect {
    public List<String> createTable(TableDefinition def) {
        return createTable(def.toTable());
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

    public List<String> setDefaultValue(String table, String column, String newDefault) {
        // DDLUtils insists on dropping and recreating the table.  So:
        return Arrays.asList(String.format(
            "ALTER TABLE %s ALTER COLUMN %s SET DEFAULT %s", table, column, sqlLiteral(newDefault)
        ));
    }

    public List<String> setNullable(String table, String column, boolean nullable) {
        return Arrays.asList(String.format(
            "ALTER TABLE %s ALTER COLUMN %s SET %sNULL", table, column, nullable ? "" : "NOT "
        ));
    }

    ////// UTILITIES

    private List<String> getSqlForChanges(ModelChange... changes) {
        return separateStatements(getPlatform().getChangeDatabaseSql(Arrays.asList(changes)));
    }

}
