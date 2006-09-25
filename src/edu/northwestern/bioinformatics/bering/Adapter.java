package edu.northwestern.bioinformatics.bering;

import org.apache.ddlutils.model.Column;
import edu.northwestern.bioinformatics.bering.runtime.Version;

import java.sql.SQLException;
import java.util.regex.Pattern;
import java.util.List;

/**
 * @author Moses Hohman
 */
public interface Adapter {
    void beginTransaction();

    void commit();

    void rollback();

    void close();

    void createTable(TableDefinition def);

    void dropTable(String name, boolean hasPrimaryKey);

    void addColumn(String tableName, Column column);

    void removeColumn(String tableName, String columnName);

    void renameColumn(String tableName, String columnName, String newColumnName);

    void setDefaultValue(String tableName, String columnName, String newDefault);

    void setNullable(String tableName, String columnName, boolean nullable);

    void execute(String sql);

    void insert(String tableName, List<String> columnNames, List<Object> values);

    String getDatabaseName();

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
