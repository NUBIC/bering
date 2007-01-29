package edu.northwestern.bioinformatics.bering.dialect;

import org.apache.ddlutils.Platform;
import org.apache.ddlutils.alteration.AddColumnChange;
import org.apache.ddlutils.alteration.TableChange;
import org.apache.ddlutils.alteration.RemoveColumnChange;
import org.apache.ddlutils.alteration.ModelChange;
import org.apache.ddlutils.util.SqlTokenizer;
import org.apache.ddlutils.model.Table;
import org.apache.ddlutils.model.Column;

import java.util.List;
import java.util.LinkedList;
import java.util.Arrays;

import edu.northwestern.bioinformatics.bering.TableDefinition;

/**
 * @author Rhett Sutphin
 */
public abstract class DdlUtilsBasedDialect implements Dialect {
    private Platform platform;

    public String getDialectName() {
        return getPlatform().getName();
    }

    public Platform getPlatform() {
        return platform;
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
    }

    public List<String> createTable(TableDefinition def) {
        return createTable(def.toTable());
    }// Uses DdlUtils' tokenizer, which just splits on ;
    public List<String> separateStatements(String script) {
        SqlTokenizer tok = new SqlTokenizer(script);
        List<String> stmts = new LinkedList<String>();
        while (tok.hasMoreStatements()) {
            stmts.add(tok.getNextStatement().trim());
        }
        return stmts;
    }

    protected List<String> createTable(Table table) {
        return separateStatements(
            getPlatform().getCreateTablesSql(DdlUtilsTools.createDatabase(table), false, false)
        );
    }

    public List<String> dropTable(String tableName, boolean hasPrimaryKey) {
        return dropTable(createTable(tableName, hasPrimaryKey));
    }

    private static Table createTable(String tableName, boolean hasPrimaryKey) {
        return hasPrimaryKey ? DdlUtilsTools.createIdedTable(tableName) : DdlUtilsTools.createTable(tableName);
    }

    protected List<String> dropTable(Table table) {
        return separateStatements(
            getPlatform().getDropTablesSql(DdlUtilsTools.createDatabase(table), false)
        );
    }

    public List<String> addColumn(String tableName, Column column) {
        Table table = DdlUtilsTools.createTable(tableName);
        AddColumnChange addColumn = new AddColumnChange(table, column, null, null);
        addColumn.setAtEnd(true);
        return getSqlForChanges(addColumn);
    }

    public List<String> dropColumn(String tableName, String columnName) {
        Table table = DdlUtilsTools.createIdedTable(tableName);
        Column column = DdlUtilsTools.createColumn(columnName);
        TableChange removeColumn = new RemoveColumnChange(table, column);
        return getSqlForChanges(removeColumn);
    }

    private List<String> getSqlForChanges(ModelChange... changes) {
        return separateStatements(getPlatform().getChangeDatabaseSql(Arrays.asList(changes)));
    }
}
