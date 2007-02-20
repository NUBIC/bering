package edu.northwestern.bioinformatics.bering;

import static edu.northwestern.bioinformatics.bering.Migration.PRIMARY_KEY_KEY;
import org.easymock.classextension.EasyMock;

import java.sql.Types;
import java.util.Arrays;
import java.util.Collections;
import static java.util.Collections.singletonMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author rsutphin
 */
public class MigrationTest extends BeringTestCase {
    private Adapter adapter;
    private Migration migration = new StubMigration();

    protected void setUp() throws Exception {
        super.setUp();
        adapter = registerMockFor(Adapter.class);
        migration.setAdapter(adapter);
    }

    public void testCreatedColumnTypes() throws Exception {
        assertEquals(Types.BIT,       getCreatedColumnType("boolean"));
        assertEquals(Types.DATE,      getCreatedColumnType("date"));
        assertEquals(Types.TIME,      getCreatedColumnType("time"));
        assertEquals(Types.TIMESTAMP, getCreatedColumnType("timestamp"));
        assertEquals(Types.VARCHAR,   getCreatedColumnType("string"));
        assertEquals(Types.FLOAT,     getCreatedColumnType("float"));
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

    public void testRenameTablePkTrue() throws Exception {
        String tableName = "table";
        String newName = "newName";
        adapter.renameTable(tableName, newName, true);
        replayMocks();

        migration.renameTable(
            singletonMap(Migration.PRIMARY_KEY_KEY, (Object) Boolean.TRUE), tableName, newName);
        verifyMocks();
    }

    public void testRenameTablePkFalse() throws Exception {
        String tableName = "table";
        String newName = "newName";
        adapter.renameTable(tableName, newName, false);
        replayMocks();

        migration.renameTable(
            singletonMap(Migration.PRIMARY_KEY_KEY, (Object) Boolean.FALSE), tableName, newName);
        verifyMocks();
    }

    public void testRenameTablePkNotPresent() throws Exception {
        String tableName = "table";
        String newName = "newName";
        adapter.renameTable(tableName, newName, true);
        replayMocks();

        migration.renameTable(
            singletonMap("foo", (Object) "bar"), tableName, newName);
        verifyMocks();
    }

    public void testInsert() {
        String tableName = "table";
        Map<String,  Object> expectedValues = new LinkedHashMap<String, Object>();
        expectedValues.put("1", 1);
        expectedValues.put("2", "two");
        expectedValues.put("3", "eighteen");

        adapter.insert(tableName, Arrays.asList("1", "2", "3"), Arrays.asList(1, (Object) "two", "eighteen"), true);
        replayMocks();

        migration.insert(tableName, expectedValues);
        verifyMocks();
    }

    public void testInsertNoPk() throws Exception {
        String tableName = "table";

        adapter.insert(tableName, Arrays.asList("1"), Arrays.asList((Object) "one"), false);
        replayMocks();

        migration.insert(Collections.singletonMap(PRIMARY_KEY_KEY, (Object) Boolean.FALSE),
            tableName, Collections.singletonMap("1", (Object) "one"));
        verifyMocks();
    }

    public void testInsertExplicitPk() throws Exception {
        String tableName = "table";

        adapter.insert(tableName, Arrays.asList("1"), Arrays.asList((Object) "one"), true);
        replayMocks();

        migration.insert(Collections.singletonMap(PRIMARY_KEY_KEY, (Object) Boolean.TRUE),
            tableName, Collections.singletonMap("1", (Object) "one"));
        verifyMocks();
    }

    private void expectDbName(String dbName) {
        EasyMock.expect(adapter.getDatabaseName()).andReturn(dbName).anyTimes();
    }

    private int getCreatedColumnType(String columnType) {
        return Column.createColumn(null, "", columnType).getTypeCode();
    }
}
