package edu.northwestern.bioinformatics.bering;

import junit.framework.TestCase;

import java.util.Collections;

/**
 * @author Moses Hohman
 */
public class TableDefinitionTest extends TestCase {
    public void testNewTableContainsPrimaryKey() {
        TableDefinition definition = new TableDefinition("frogs");
        assertEquals("CREATE TABLE frogs (\n\tid INTEGER PRIMARY KEY\n)", definition.toSql());
    }

    public void testToSqlForTableWithColumns() {
        TableDefinition definition = new TableDefinition("frogs");
        definition.addColumn(Collections.singletonMap("limit", "30"), "name", "string");
        assertEquals("CREATE TABLE frogs (\n" +
                "\tid INTEGER PRIMARY KEY,\n" +
                "\tname VARCHAR(30)\n" +
                ")", definition.toSql());
    }
}
