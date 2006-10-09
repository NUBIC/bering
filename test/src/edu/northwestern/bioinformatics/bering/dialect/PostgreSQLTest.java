package edu.northwestern.bioinformatics.bering.dialect;

/**
 * @author Rhett Sutphin
 */
public class PostgreSQLTest extends DdlUtilsDialectTestCase<PostgreSQL> {
    protected PostgreSQL createDialect() { return new PostgreSQL(); }

    public void testSetNullable() throws Exception {
        assertStatements(
            getDialect().setNullable("feast", "length", true),
            "ALTER TABLE feast ALTER COLUMN length DROP NOT NULL"
        );
    }

    public void testSetNotNullable() throws Exception {
        assertStatements(
            getDialect().setNullable("feast", "length", false),
            "ALTER TABLE feast ALTER COLUMN length SET NOT NULL"
        );
    }

    public void testRenameTableWithPk() throws Exception {
        String tableName = "test";
        String newTableName = "t_test";

        assertStatements(
            getDialect().renameTable(tableName, newTableName, true),
            "ALTER TABLE test RENAME TO t_test",
            "ALTER TABLE test_id_seq RENAME TO t_test_id_seq"
        );
    }

    public void testRenameTableWithoutPk() throws Exception {
        String tableName = "test";
        String newTableName = "t_test";

        assertStatements(
            getDialect().renameTable(tableName, newTableName, false),
            "ALTER TABLE test RENAME TO t_test"
        );
    }
}
