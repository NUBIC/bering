package edu.northwestern.bioinformatics.bering;

import org.apache.ddlutils.model.Column;
import edu.northwestern.bioinformatics.bering.runtime.Version;

/**
 * @author Moses Hohman
 */
public interface Adapter {
/*  // TODO:
    void beginTransaction();

    void commit();

    void rollback();
*/

    void createTable(TableDefinition def);

    void dropTable(String name);

    void addColumn(String tableName, Column column);

    void removeColumn(String tableName, String columnName);

    Version loadVersions();

    void updateVersion(Integer release, Integer migration);
}
