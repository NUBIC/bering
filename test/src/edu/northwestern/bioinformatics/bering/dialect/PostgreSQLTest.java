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
}
