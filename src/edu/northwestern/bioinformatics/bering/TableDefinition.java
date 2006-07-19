package edu.northwestern.bioinformatics.bering;

import org.apache.ddlutils.model.Column;
import org.apache.ddlutils.model.Table;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Moses Hohman
 */
public class TableDefinition {
    private String name;
    private List<Column> columns;
    private Migration context;

    public TableDefinition(String name, Migration context) {
        this.name = name;
        this.context = context;

        columns = new LinkedList<Column>();
        columns.add(createPrimaryKeyColumn());
    }

    private Column createPrimaryKeyColumn() {
        Column col = context.createColumn(null, "id", "integer");
        col.setPrimaryKey(true);
        col.setAutoIncrement(true);
        return col;
    }

    public void addColumn(String columnName, String type) {
        addColumn(null, columnName, type);
    }

    public void addColumn(Map<String, Object> parameters, String columnName, String type) {
        columns.add(context.createColumn(parameters, columnName, type));
    }

    public Table toTable() {
        Table t =  new Table();
        t.setName(getName());
        for (Column column : columns) {
            t.addColumn(column);
        }
        return t;
    }

    public String getName() {
        return name;
    }
}
