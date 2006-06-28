package edu.northwestern.bioinformatics.bering;

import junit.framework.TestCase;
import org.apache.ddlutils.model.Column;
import org.apache.ddlutils.model.Table;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Moses Hohman
 */
public class TableDefinitionTest extends TestCase {
    private TableDefinition definition;

    protected void setUp() throws Exception {
        super.setUp();
        definition = new TableDefinition("frogs", new StubMigration());
    }

    public void testNewTableContainsPrimaryKey() {
        Table created = definition.toTable();
        Column idCol = created.findColumn("id");
        assertNotNull("No id column", idCol);
        assertTrue("id column isn't the primary key", idCol.isPrimaryKey());
    }

    public void testToTablePreservesTableName() throws Exception {
        assertEquals("frogs", definition.toTable().getName());
    }

    public void testAddColumnWithLimit() throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        params.put("limit", "50");
        definition.addColumn(params, "name", "string");

        Column col = definition.toTable().findColumn("name");
        assertNotNull(col);
        assertEquals("Wrong size", 50, col.getSizeAsInt());
    }

    public void testAddColumnWithPrecision() throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        params.put("precision", "50");
        definition.addColumn(params, "name", "string");

        Column col = definition.toTable().findColumn("name");
        assertNotNull(col);
        assertEquals("Wrong scale", 50, col.getScale());
    }

    public void testAddNonNullableColumn() throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        params.put("nullable", "false");
        definition.addColumn(params, "name", "string");

        Column col = definition.toTable().findColumn("name");
        assertNotNull(col);
        assertTrue("Should be required", col.isRequired());
    }

    public void testAddNullableColumn() throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        params.put("nullable", "true");
        definition.addColumn(params, "name", "string");

        Column col = definition.toTable().findColumn("name");
        assertNotNull(col);
        assertFalse("Should not be required", col.isRequired());
    }

}
