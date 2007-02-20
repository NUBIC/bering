package edu.northwestern.bioinformatics.bering;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

/**
 * @author Moses Hohman
 * @author Rhett Sutphin
 */
public class TableDefinition {
    private String name;
    private List<Column> columns;
    private boolean includeDefaultPrimaryKey;

    public TableDefinition(String name) {
        this.name = name;
        columns = new LinkedList<Column>();
        includeDefaultPrimaryKey = true;
    }

    public void addColumn(String columnName, String type) {
        addColumn(null, columnName, type);
    }

    public void addColumn(Map<String, Object> parameters, String columnName, String type) {
        columns.add(Column.createColumn(parameters, columnName, type));
    }

    public void addVersionColumn() {
        columns.add(Column.VERSION_COLUMN);
    }

    public boolean getIncludePrimaryKey() {
        return includeDefaultPrimaryKey;
    }

    public void setIncludePrimaryKey(boolean set) {
        includeDefaultPrimaryKey = set;
    }

    public String getName() {
        return name;
    }

    public Column findColumn(String toFind) {
        for (Column column : columns) {
            if (toFind.equals(column.getName())) {
                return column;
            }
        }
        return null;
    }

    public int getColumnCount() {
        return columns.size() + (includeDefaultPrimaryKey ? 1 : 0);
    }

    public List<Column> getColumns() {
        List<Column> finalCols = new ArrayList<Column>(1 + columns.size());
        if (includeDefaultPrimaryKey) finalCols.add(Column.AUTOMATIC_PK);
        finalCols.addAll(columns);
        return finalCols;
    }
}
