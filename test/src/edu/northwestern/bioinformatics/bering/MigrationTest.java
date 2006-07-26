package edu.northwestern.bioinformatics.bering;

import static edu.northwestern.bioinformatics.bering.Migration.*;
import junit.framework.TestCase;
import org.apache.ddlutils.model.Column;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

/**
 * @author rsutphin
 */
public class MigrationTest extends TestCase {
    private StubAdapter adapter = new StubAdapter();
    private Migration migration = new StubMigration();
    private Map<String, Object> parameters = new HashMap<String, Object>();

    protected void setUp() throws Exception {
        super.setUp();
        migration.setAdapter(adapter);
    }

    public void testCreateColumn() throws Exception {
        Column actual = migration.createColumn(null, "title", "string");
        assertEquals("Wrong name", "title", actual.getName());
        assertEquals("Wrong type", Types.VARCHAR, actual.getTypeCode());
        assertFalse("Should be nullable by default", actual.isRequired());
        assertNull("Should have no size by default", actual.getSize());
    }

    public void testCreateNotNullColumn() throws Exception {
        parameters.put(NULLABLE_KEY, false);
        Column actual = migration.createColumn(parameters, "notnull", "string");
        assertTrue("required not set correctly", actual.isRequired());
    }

    public void testCreateColumnWithIntegerDefaultValue() {
        parameters.put("defaultValue", 0);
        Column actual = migration.createColumn(parameters, "position", "integer");
        assertEquals("0", actual.getDefaultValue());
    }

    public void testCreateColumnWithStringDefaultValue() {
        parameters.put("defaultValue", "days");
        Column actual = migration.createColumn(parameters, "duration_unit", "string");
        assertEquals("days", actual.getDefaultValue());
    }

    public void testCreateColumnWithNullDefaultValueDoesntDoAnything() {
        parameters.put("defaultValue", null);
        Column actual = migration.createColumn(parameters, "duration_unit", "string");
        assertNull("default value not null", actual.getDefaultValue());
    }

    public void testCreateColumnWithLimit() throws Exception {
        parameters.put(LIMIT_KEY, 255);
        Column actual = migration.createColumn(parameters, "notnull", "string");
        assertEquals(255, actual.getSizeAsInt());
    }

    public void testCreateColumnWithPrecision() throws Exception {
        parameters.put(PRECISION_KEY, 9);
        Column actual = migration.createColumn(parameters, "notnull", "string");
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

    public void testDatabaseMatchesExact() throws Exception {
        adapter.setDatabaseName("SuperSQL");
        assertTrue(migration.databaseMatches("SuperSQL"));
    }

    public void testDatabaseMatchesSubstring() throws Exception {
        adapter.setDatabaseName("SuperSQL");
        assertTrue(migration.databaseMatches("Super"));
        assertTrue(migration.databaseMatches("uperSQ"));
    }

    public void testDatabaseMatchesCaseInsensitive() throws Exception {
        adapter.setDatabaseName("SuperSQL");
        assertTrue(migration.databaseMatches("supersql"));
        assertTrue(migration.databaseMatches("super"));
    }

    private int getCreatedColumnType(String columnType) {
        return migration.createColumn(null, "", columnType).getTypeCode();
    }
}
