package edu.northwestern.bioinformatics.bering;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Moses Hohman
 */
public class TableDefinition {
    private String name;
    private List<ColumnDefinition> columns;
    private static final String NULLABLE_KEY = "nullable";

    public TableDefinition(String name) {
        this.name = name;
        columns = new LinkedList<ColumnDefinition>();
        columns.add(new PrimaryKeyDefinition("id", "integer"));
    }

    public void addColumn(String name, String type) {
        addColumn(null, name, type);
    }

    public void addColumn(HashMap parameters, String name, String type) {
        ColumnDefinition definition = new ColumnDefinition(name, type);
        if (parameters != null && !parameters.isEmpty()) {
            if (parameters.containsKey(NULLABLE_KEY)) {
                definition.setNullable("true".equals(parameters.get(NULLABLE_KEY)));
            }
        }
        columns.add(definition);
    }

    public String toSql() {
        StringBuilder result = new StringBuilder("CREATE TABLE ");
        result.append(name).append(" (\n");
        for (ColumnDefinition column : columns) {
            result.append("\t").append(column.toSql()).append(",\n");
        }
        return result.append(")\n").toString();
    }
}
