package edu.northwestern.bioinformatics.bering;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Moses Hohman
 */
public class TableDefinition {
    private String name;
    private List<ColumnDefinition> columns;
    private static final String NULLABLE_KEY = "nullable";
    private static final String LIMIT_KEY = "limit";

    public TableDefinition(String name) {
        this.name = name;
        columns = new LinkedList<ColumnDefinition>();
        columns.add(new PrimaryKeyDefinition("id", "integer"));
    }

    public void addColumn(String name, String type) {
        addColumn(null, name, type);
    }

    public void addColumn(Map<String, String> parameters, String name, String type) {
        ColumnDefinition definition = new ColumnDefinition(name, type);
        if (parameters != null && !parameters.isEmpty()) {
            if (parameters.containsKey(NULLABLE_KEY)) {
                definition.setNullable("true".equals(parameters.get(NULLABLE_KEY)));
            }
            if (parameters.containsKey(LIMIT_KEY)) {
                definition.setLimit(Integer.parseInt(parameters.get(LIMIT_KEY)));
            }
        }
        columns.add(definition);
    }

    public String toSql() {
        StringBuilder result = new StringBuilder("CREATE TABLE ");
        result.append(name).append(" (\n");
        for (int i = 0; i < columns.size(); i++) {
            if (i!=0) {
                result.append(",\n");
            }
            result.append("\t").append(columns.get(i).toSql());
        }
        return result.append("\n)").toString();
    }
}
