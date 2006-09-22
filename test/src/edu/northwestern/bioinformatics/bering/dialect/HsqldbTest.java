package edu.northwestern.bioinformatics.bering.dialect;

/**
 * @author Rhett Sutphin
 */
public class HsqldbTest extends DdlUtilsDialectTestCase<Hsqldb> {
    protected Hsqldb createDialect() {
        return new Hsqldb();
    }

    public void testRenameColumn() throws Exception {
        assertStatements(
            getDialect().renameColumn("feast", "length", "duration"),
            "ALTER TABLE feast ALTER COLUMN length RENAME TO duration"
        );
    }
}
