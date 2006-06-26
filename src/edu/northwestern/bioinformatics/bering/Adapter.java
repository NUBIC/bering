package edu.northwestern.bioinformatics.bering;

import org.apache.ddlutils.model.Column;

/**
 * @author Moses Hohman
 */
public interface Adapter {
    void createTable(TableDefinition def);

    void dropTable(String name);

    Column createPrimaryKeyColumn(String name);

    int getTypeCode(String typeName);
}
