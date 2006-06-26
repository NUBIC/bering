package edu.northwestern.bioinformatics.bering;

import junit.framework.TestCase;

/**
 * @author Moses Hohman
 */
public class ColumnDefinitionTest extends TestCase {

    public void testToSqlForSimpleColumn() {
        ColumnDefinition definition = new ColumnDefinition("num_apples", "integer");
        assertEquals("num_apples INTEGER", definition.toSql());
    }

    public void testToSqlForNonNullableColumn() {
        ColumnDefinition definition = new ColumnDefinition("num_apples", "integer");
        definition.setNullable(false);
        assertEquals("num_apples INTEGER NOT NULL", definition.toSql());
    }
}
