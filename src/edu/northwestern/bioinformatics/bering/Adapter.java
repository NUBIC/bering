package edu.northwestern.bioinformatics.bering;

/**
 * @author Moses Hohman
 */
public interface Adapter {
    void createTable(TableDefinition def);

    void dropTable(String name);

    int getTypeCode(String typeName);
}
