package edu.northwestern.bioinformatics.bering;

import org.apache.ddlutils.model.Column;
import org.apache.ddlutils.model.Table;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;

import java.beans.PropertyEditor;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Moses Hohman
 */
public class TableDefinition {
    private String name;
    private List<Column> columns;
    private Adapter adapter;
    private static final String NULLABLE_KEY  = "nullable";
    private static final String LIMIT_KEY     = "limit";
    private static final String PRECISION_KEY = "precision";

    public TableDefinition(String name, Adapter adapter) {
        this.name = name;
        this.adapter = adapter;

        columns = new LinkedList<Column>();
        columns.add(createPrimaryKeyColumn());
    }

    private Column createPrimaryKeyColumn() {
        Column col = new Column();
        col.setPrimaryKey(true);
        col.setName("id");
        col.setTypeCode(adapter.getTypeCode("integer"));
        return col;
    }

    public void addColumn(String columnName, String type) {
        addColumn(null, columnName, type);
    }

    public void addColumn(Map<String, String> parameters, String columnName, String type) {
        Column col = new Column();
        col.setName(columnName);
        col.setTypeCode(adapter.getTypeCode(type));
        if (parameters != null && !parameters.isEmpty()) {
            if (parameters.containsKey(NULLABLE_KEY)) {
                PropertyEditor editor = new CustomBooleanEditor(false);
                editor.setAsText(parameters.get(NULLABLE_KEY));
                col.setRequired(!((Boolean) editor.getValue()));
            }
            if (parameters.containsKey(LIMIT_KEY)) {
                col.setSize(parameters.get(LIMIT_KEY));
            }
            if (parameters.containsKey(PRECISION_KEY)) {
                col.setScale(new Integer(parameters.get(PRECISION_KEY)));
            }
        }
        columns.add(col);
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
