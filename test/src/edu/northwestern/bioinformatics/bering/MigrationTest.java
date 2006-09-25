package edu.northwestern.bioinformatics.bering;

import static java.util.Collections.singletonMap;

import static edu.northwestern.bioinformatics.bering.Migration.*;
import org.apache.ddlutils.model.Column;
import org.easymock.classextension.EasyMock;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Arrays;
import java.util.Collections;

/**
 * @author rsutphin
 */
public class MigrationTest extends BeringTestCase {
    private Adapter adapter;
    private Migration migration = new StubMigration();
    private Map<String, Object> parameters = new HashMap<String, Object>();

    protected void setUp() throws Exception {
        super.setUp();
        adapter = registerMockFor(Adapter.class);
        migration.setAdapter(adapter);
    }

    public void testCreateColumn() throws Exception {
        Column actual = Migration.createColumn(null, "title", "string");
        assertEquals("Wrong name", "title", actual.getName());
        assertEquals("Wrong type", Types.VARCHAR, actual.getTypeCode());
        assertFalse("Should be nullable by default", actual.isRequired());
        assertNull("Should have no size by default", actual.getSize());
    }

    public void testCreateNotNullColumn() throws Exception {
        parameters.put(NULLABLE_KEY, false);
        Column actual = Migration.createColumn(parameters, "notnull", "string");
        assertTrue("required not set correctly", actual.isRequired());
    }

    public void testCreateColumnWithIntegerDefaultValue() {
        parameters.put("defaultValue", 0);
        Column actual = Migration.createColumn(parameters, "position", "integer");
        assertEquals("0", actual.getDefaultValue());
    }

    public void testCreateColumnWithStringDefaultValue() {
        parameters.put("defaultValue", "days");
        Column actual = Migration.createColumn(parameters, "duration_unit", "string");
        assertEquals("days", actual.getDefaultValue());
    }

    public void testCreateColumnWithNullDefaultValueDoesntDoAnything() {
        parameters.put("defaultValue", null);
        Column actual = Migration.createColumn(parameters, "duration_unit", "string");
        assertNull("default value not null", actual.getDefaultValue());
    }

    public void testCreateColumnWithLimit() throws Exception {
        parameters.put(LIMIT_KEY, 255);
        Column actual = Migration.createColumn(parameters, "notnull", "string");
        assertEquals(255, actual.getSizeAsInt());
    }

    public void testCreateColumnWithPrecision() throws Exception {
        parameters.put(PRECISION_KEY, 9);
        Column actual = Migration.createColumn(parameters, "notnull", "string");
        assertEquals(9, actual.getScale());
    }

    public void testCreateManualPrimaryKeyColumn() throws Exception {
        parameters.put(PRIMARY_KEY_KEY, true);
        Column actual = Migration.createColumn(parameters, "name", "string");
        assertTrue(actual.isPrimaryKey());
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
        expectDbName("SuperSQL");
        replayMocks();
        assertTrue(migration.databaseMatches("SuperSQL"));
        verifyMocks();
    }

    public void testDatabaseMatchesSubstring() throws Exception {
        expectDbName("SuperSQL");
        replayMocks();
        assertTrue(migration.databaseMatches("Super"));
        assertTrue(migration.databaseMatches("uperSQ"));
        verifyMocks();
    }

    public void testDatabaseMatchesCaseInsensitive() throws Exception {
        expectDbName("SuperSQL");
        replayMocks();
        assertTrue(migration.databaseMatches("supersql"));
        assertTrue(migration.databaseMatches("super"));
        verifyMocks();
    }

    public void testDropTableNoParameters() throws Exception {
        String tableName = "table";
        adapter.dropTable(tableName, true);
        replayMocks();

        migration.dropTable(tableName);
        verifyMocks();
    }

    public void testDropTablePkTrue() throws Exception {
        String tableName = "table";
        adapter.dropTable(tableName, true);
        replayMocks();

        migration.dropTable(
            singletonMap(Migration.PRIMARY_KEY_KEY, (Object) Boolean.TRUE), tableName);
        verifyMocks();
    }

    public void testDropTablePkFalse() throws Exception {
        String tableName = "table";
        adapter.dropTable(tableName, false);
        replayMocks();

        migration.dropTable(
            singletonMap(Migration.PRIMARY_KEY_KEY, (Object) Boolean.FALSE), tableName);
        verifyMocks();
    }

    public void testDropTablePkNotPresent() throws Exception {
        String tableName = "table";
        adapter.dropTable(tableName, true);
        replayMocks();

        migration.dropTable(
            singletonMap("foo", (Object) "bar"), tableName);
        verifyMocks();
    }

    public void testInsert() {
        String tableName = "table";
        Map<String,  Object> expectedValues = new LinkedHashMap<String, Object>();
        expectedValues.put("1", 1);
        expectedValues.put("2", "two");
        expectedValues.put("3", "eighteen");

        adapter.insert(tableName, Arrays.asList("1", "2", "3"), Arrays.asList(1, (Object) "two", "eighteen"));
        replayMocks();

        migration.insert(tableName, expectedValues);
        verifyMocks();
    }

    private void expectDbName(String dbName) {
        EasyMock.expect(adapter.getDatabaseName()).andReturn(dbName).anyTimes();
    }

    private int getCreatedColumnType(String columnType) {
        return Migration.createColumn(null, "", columnType).getTypeCode();
    }
}
