package edu.northwestern.bioinformatics.bering;

import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Map;
import java.sql.Types;

/**
 * @author Moses Hohman
 */
public class TableDefinitionTest extends TestCase {
    private TableDefinition definition;
    private Map<String, Object> params;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        definition = new TableDefinition("frogs");
        params = new HashMap<String, Object>();
    }

    public void testNewTableContainsPrimaryKey() {
        assertTrue(definition.getColumns().contains(Column.AUTOMATIC_PK));
    }
    
    public void testIdCanBeDisabledAfterCreation() throws Exception {
        TableDefinition noId = new TableDefinition("anonymous");
        assertEquals(1, noId.getColumnCount());
        noId.setIncludePrimaryKey(false);
        assertEquals(0, noId.getColumnCount());
    }

    public void testAddColumnWithLimit() throws Exception {
        params.put("limit", 50);
        definition.addColumn(params, "name", "string");

        Column name = definition.findColumn("name");
        assertNotNull(name);
        assertEquals("Wrong size", 50, (int) name.getTypeQualifiers().getLimit());
    }

    public void testAddColumnWithPrecision() throws Exception {
        params.put("precision", 50);
        definition.addColumn(params, "name", "string");

        Column name = definition.findColumn("name");
        assertNotNull(name);
        assertEquals("Wrong scale", 50, (int) name.getTypeQualifiers().getPrecision());
    }

    public void testAddNonNullableColumn() throws Exception {
        params.put("nullable", false);
        definition.addColumn(params, "name", "string");

        Column name = definition.findColumn("name");
        assertNotNull(name);
        assertFalse("Should be required", name.isNullable());
    }

    public void testAddNullableColumn() throws Exception {
        params.put("nullable", true);
        definition.addColumn(params, "name", "string");

        Column name = definition.findColumn("name");
        assertNotNull(name);
        assertTrue("Should not be required", name.isNullable());
    }

    public void testAddColumnWithIntegerDefaultValue() {
        params.put("defaultValue", 0);
        definition.addColumn(params, "position", "integer");

        Column position = definition.findColumn("position");
        assertEquals("0", position.getDefaultValue());
    }

    public void testAddVersionColumn() throws Exception {
        definition.addVersionColumn();

        Column actual = definition.findColumn("version");
        assertNotNull("Version column not added", actual);
        assertEquals("Wrong name", "version", actual.getName());
        assertEquals("Wrong type", Types.INTEGER, actual.getTypeCode());
        assertFalse("Not NOT NULL", actual.isNullable());
        assertEquals("Default not zero", "0", actual.getDefaultValue());
    }
}
