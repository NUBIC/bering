package edu.northwestern.bioinformatics.bering;

/**
 * @author Moses Hohman
 */
public class PrimaryKeyDefinition extends ColumnDefinition {
    public PrimaryKeyDefinition(String name, String type) {
        super(name, type);
    }

    public String toSql() {
        return new StringBuilder(super.toSql()).append(" PRIMARY KEY").toString();
    }
}
