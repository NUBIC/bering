package edu.northwestern.bioinformatics.bering.dialect;

import edu.northwestern.bioinformatics.bering.Column;

/**
 * @author Rhett Sutphin
 */
public abstract class HibernateBasedDialectTestCase<D extends HibernateBasedDialect> extends DialectTestCase<D> {

    /* Require these tests to make sure that all types are properly supported in all dialects */

    public void testAddStringColumn() throws Exception {
        assertStatements(
            getDialect().addColumn("t", Column.createColumn(null, "c", "string")),
            expectedAddStringStatement()
        );
    }

    public void testAddIntegerColumn() throws Exception {
        assertStatements(
            getDialect().addColumn("t", Column.createColumn(null, "c", "integer")),
            expectedAddIntegerStatement()
        );
    }

    public void testAddFloatColumn() throws Exception {
        assertStatements(
            getDialect().addColumn("t", Column.createColumn(null, "c", "float")),
            expectedAddFloatStatement()
        );
    }

    public void testAddBooleanColumn() throws Exception {
        assertStatements(
            getDialect().addColumn("t", Column.createColumn(null, "c", "boolean")),
            expectedAddBooleanStatement()
        );
    }

    public void testAddDateColumn() throws Exception {
        assertStatements(
            getDialect().addColumn("t", Column.createColumn(null, "c", "date")),
            expectedAddDateStatement()
        );
    }

    public void testAddTimeColumn() throws Exception {
        assertStatements(
            getDialect().addColumn("t", Column.createColumn(null, "c", "time")),
            expectedAddTimeStatement()
        );
    }

    public void testAddTimestampColumn() throws Exception {
        assertStatements(
            getDialect().addColumn("t", Column.createColumn(null, "c", "timestamp")),
            expectedAddTimestampStatement()
        );
    }

    protected abstract String expectedAddStringStatement();

    protected abstract String expectedAddIntegerStatement();

    protected abstract String expectedAddFloatStatement();

    protected abstract String expectedAddBooleanStatement();

    protected abstract String expectedAddDateStatement();

    protected abstract String expectedAddTimeStatement();

    protected abstract String expectedAddTimestampStatement();
}
