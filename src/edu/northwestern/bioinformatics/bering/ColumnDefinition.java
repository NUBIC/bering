package edu.northwestern.bioinformatics.bering;

/**
 * @author Moses Hohman
 */
public class ColumnDefinition {
    private String name;
    private String type;
    private boolean nullable = true;

    public ColumnDefinition(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public boolean isNullable() {
        return nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    private String getSqlType() {
        // TODO: fill this out
        String result = "UNSUPPORTED TYPE";
        if ("integer".equals(type)) {
            result = "INTEGER";
        } else if ("string".equals(type)) {
            result = "VARCHAR";
        }
        return result;
    }

    public String toSql() {
        StringBuilder result = new StringBuilder(name);
        result.append(" ").append(getSqlType());
        if (!nullable) {
            result.append(" ").append("NOT NULL");
        }
        return result.toString();
    }
}
