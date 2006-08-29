package edu.northwestern.bioinformatics.bering;

import junit.framework.TestCase;
import org.apache.ddlutils.model.Column;
import org.apache.ddlutils.model.Table;

import java.util.HashMap;
import java.util.Map;
import java.sql.Types;

/**
 * @author Moses Hohman
 */
public class TableDefinitionTest extends TestCase {
    private TableDefinition definition;
    private Map<String, Object> params;

    protected void setUp() throws Exception {
        super.setUp();
        definition = new TableDefinition("frogs", new StubMigration());
        params = new HashMap<String, Object>();
    }

    public void testNewTableContainsPrimaryKey() {
        Table created = definition.toTable();
        Column id = created.findColumn("id");
        assertNotNull("No id column", id);
        assertTrue("id column isn't the primary key", id.isPrimaryKey());
        assertTrue("id column isn't autoincrement", id.isAutoIncrement());
    }

    public void testToTablePreservesTableName() throws Exception {
        assertEquals("frogs", definition.toTable().getName());
    }

    public void testAddColumnWithLimit() throws Exception {
        params.put("limit", 50);
        definition.addColumn(params, "name", "string");

        Column name = definition.toTable().findColumn("name");
        assertNotNull(name);
        assertEquals("Wrong size", 50, name.getSizeAsInt());
    }

    public void testAddColumnWithPrecision() throws Exception {
        params.put("precision", 50);
        definition.addColumn(params, "name", "string");

        Column name = definition.toTable().findColumn("name");
        assertNotNull(name);
        assertEquals("Wrong scale", 50, name.getScale());
    }

    public void testAddNonNullableColumn() throws Exception {
        params.put("nullable", false);
        definition.addColumn(params, "name", "string");

        Column name = definition.toTable().findColumn("name");
        assertNotNull(name);
        assertTrue("Should be required", name.isRequired());
    }

    public void testAddNullableColumn() throws Exception {
        params.put("nullable", true);
        definition.addColumn(params, "name", "string");

        Column name = definition.toTable().findColumn("name");
        assertNotNull(name);
        assertFalse("Should not be required", name.isRequired());
    }

    public void testAddColumnWithIntegerDefaultValue() {
        params.put("defaultValue", 0);
        definition.addColumn(params, "position", "integer");

        Column position = definition.toTable().findColumn("position");
        assertEquals("0", position.getDefaultValue());
    }

    public void testAddVersionColumn() throws Exception {
        definition.addVersionColumn();

        Column actual = definition.toTable().findColumn("version");
        assertNotNull("Version column not added", actual);
        assertEquals("Wrong name", "version", actual.getName());
        assertEquals("Wrong type", Types.INTEGER, actual.getTypeCode());
        assertTrue("Not NOT NULL", actual.isRequired());
        assertEquals("Default not zero", "0", actual.getDefaultValue());
    }
}
