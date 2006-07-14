package edu.northwestern.bioinformatics.bering;

import static edu.northwestern.bioinformatics.bering.Migration.NULLABLE_KEY;
import static edu.northwestern.bioinformatics.bering.Migration.LIMIT_KEY;
import static edu.northwestern.bioinformatics.bering.Migration.PRECISION_KEY;
import junit.framework.TestCase;
import org.apache.ddlutils.model.Column;

import java.sql.Types;
import java.util.Collections;

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
        Column actual = migration.createColumn(Collections.singletonMap(NULLABLE_KEY, (Object) Boolean.FALSE), "notnull", "string");
        assertTrue("required not set correctly", actual.isRequired());
    }

    public void testCreateColumnWithLimit() throws Exception {
        Column actual = migration.createColumn(Collections.singletonMap(LIMIT_KEY, (Object) 255), "notnull", "string");
        assertEquals(255, actual.getSizeAsInt());
    }

    public void testCreateColumnWithPrecision() throws Exception {
        Column actual = migration.createColumn(Collections.singletonMap(PRECISION_KEY, (Object) 9), "notnull", "string");
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
