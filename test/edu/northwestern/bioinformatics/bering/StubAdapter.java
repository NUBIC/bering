package edu.northwestern.bioinformatics.bering;

/**
 * @author Moses Hohman
 */
public class StubAdapter implements Adapter {
    public void createTable(TableDefinition def) {
        throw new UnsupportedOperationException("createTable not implemented");
    }

    public void dropTable(String name) {
        throw new UnsupportedOperationException("dropTable not implemented");
    }

    public int getTypeCode(String typeName) {
        return 0;
    }
}
