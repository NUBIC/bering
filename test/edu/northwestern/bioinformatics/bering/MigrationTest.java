package edu.northwestern.bioinformatics.bering;

import junit.framework.TestCase;

import java.sql.Types;
import java.util.Collections;

import org.apache.ddlutils.model.Column;

/**
 * @author rsutphin
 */
public class MigrationTest extends TestCase {
    private Migration migration = new StubMigration();

    public void testCreateColumn() throws Exception {
        Column actual = migration.createColumn(null, "title", "string");
        assertEquals("Wrong name", "title", actual.getName());
        assertEquals("Wrong type", Types.VARCHAR, actual.getTypeCode());
        assertFalse("Should be nullable by default", actual.isRequired());
        assertNull("Should have no size by default", actual.getSize());
    }

    public void testCreateNotNullColumn() throws Exception {
        Column actual = migration.createColumn(Collections.singletonMap("null", "false"), "notnull", "string");
        assertTrue(actual.isRequired());
    }

    public void testCreateColumnWithLimit() throws Exception {
        Column actual = migration.createColumn(Collections.singletonMap("limit", "255"), "notnull", "string");
        assertEquals(255, actual.getSizeAsInt());
    }

    public void testCreateColumnWithPrecision() throws Exception {
        Column actual = migration.createColumn(Collections.singletonMap("precision", "9"), "notnull", "string");
        assertEquals(9, actual.getScale());
    }

    public void testCreatedColumnTypes() throws Exception {
        assertEquals(Types.BOOLEAN,   getCreatedColumnType("boolean"));
        assertEquals(Types.DATE,      getCreatedColumnType("date"));
        assertEquals(Types.TIME,      getCreatedColumnType("time"));
        assertEquals(Types.TIMESTAMP, getCreatedColumnType("timestamp"));
        assertEquals(Types.VARCHAR,   getCreatedColumnType("string"));
        assertEquals(Types.NUMERIC,   getCreatedColumnType("float"));
        assertEquals(Types.INTEGER,   getCreatedColumnType("integer"));
    }

    public void testInvalidTypeThrowsException() throws Exception {
        try {
            getCreatedColumnType("fancytype");
            fail("No exception thrown");
        } catch (IllegalArgumentException iae) {
            assertTrue(iae.getMessage().indexOf("fancytype") >= 0);
        }
    }

    private int getCreatedColumnType(String columnType) {
        return migration.createColumn(null, "", columnType).getTypeCode();
    }
}
