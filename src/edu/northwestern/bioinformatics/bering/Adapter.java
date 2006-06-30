package edu.northwestern.bioinformatics.bering;

import org.apache.ddlutils.model.Column;
import edu.northwestern.bioinformatics.bering.runtime.Version;

/**
 * @author Moses Hohman
 */
public interface Adapter {
    void beginTransaction();

    void commit();

    void rollback();

    void createTable(TableDefinition def);

    void dropTable(String name);

    void addColumn(String tableName, Column column);

    void removeColumn(String tableName, String columnName);

    Version loadVersions();

    /**
     * Update note that the database is now at the <code>migration</code> state
     * for a particular release.  If <code>migration</code> is zero, the database has had
     * none of the migrations for that release applied.
     * @param release
     * @param migration
     */
    void updateVersion(Integer release, Integer migration);
}
