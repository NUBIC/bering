package edu.northwestern.bioinformatics.bering;

import org.apache.ddlutils.model.Column;
import edu.northwestern.bioinformatics.bering.runtime.Version;

/**
 * @author Rhett Sutphin
 */
public class StubAdapter implements Adapter {
    private String databaseName;

    public void beginTransaction() {
        throw new UnsupportedOperationException("beginTransaction not implemented");
    }

    public void commit() {
        throw new UnsupportedOperationException("commit not implemented");
    }

    public void rollback() {
        throw new UnsupportedOperationException("rollback not implemented");
    }

    public void close() {
        throw new UnsupportedOperationException("close not implemented");
    }

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

    public void execute(String sql) {
        throw new UnsupportedOperationException("execute not implemented");
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public Version loadVersions() {
        throw new UnsupportedOperationException("loadVersions not implemented");
    }

    public void updateVersion(Integer release, Integer migration) {
        throw new UnsupportedOperationException("updateVersion not implemented");
    }
}
