package edu.northwestern.bioinformatics.bering;

import org.apache.ddlutils.model.Column;

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

    public void addColumn(String tableName, Column column) {
        throw new UnsupportedOperationException("addColumn not implemented");
    }

    public void removeColumn(String tableName, String columnName) {
        throw new UnsupportedOperationException("removeColumn not implemented");
    }

    public int getTypeCode(String typeName) {
        return 0;
    }
}
