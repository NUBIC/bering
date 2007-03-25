package edu.northwestern.bioinformatics.bering;

import static edu.northwestern.bioinformatics.bering.DatabaseAdapter.VERSION_TABLE_NAME;
import edu.northwestern.bioinformatics.bering.dialect.Hsqldb;
import edu.northwestern.bioinformatics.bering.runtime.Version;
import junit.framework.TestCase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author rsutphin
 */
public class DatabaseAdapterTest extends TestCase {
    private static final String TABLE_NAME = "books";

    private DatabaseAdapter adapter;
    private Connection conn;
    private Statement stmt;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Class.forName("org.hsqldb.jdbcDriver");
        // in-memory databases apparently persist across connections within the same JVM
        // hence the random number added to the name
        conn = DriverManager.getConnection("jdbc:hsqldb:mem:test" + Math.random(), "sa", "");
        conn.setAutoCommit(false);
        adapter = new DatabaseAdapter(conn, new Hsqldb());
        stmt = conn.createStatement();

        stmt.execute("CREATE TABLE " + TABLE_NAME + " (id IDENTITY PRIMARY KEY, title VARCHAR(50))");
        stmt.execute("INSERT INTO " + TABLE_NAME + " (id, title) VALUES (1, 'Bering is Groovy')");

        assertTablePresent(TABLE_NAME);
    }

    @Override
    protected void tearDown() throws Exception {
        conn.close();
        super.tearDown();
    }

    public void testCreateTable() throws Exception {
        String tableName = "authors";
        assertTableNotPresent(tableName);

        TableDefinition def = new TableDefinition(tableName);
        def.addColumn("name", "string");
        def.addColumn("birthdate", "timestamp");
        adapter.createTable(def);

        assertTablePresent(tableName);
        // in HSQL, the id should be automatically inserted
        stmt.execute("INSERT INTO " + tableName + "(name, birthdate) VALUES ('Dave', '2006-04-15')");
        // expect no error
    }

    public void testDropTable() throws Exception {
        assertTablePresent(TABLE_NAME);

        adapter.dropTable(TABLE_NAME, true);
        assertTableNotPresent(TABLE_NAME);
    }

    public void testRenameTable() throws Exception {
        String newName = "tomes";
        assertTablePresent(TABLE_NAME);

        adapter.renameTable(TABLE_NAME, newName, true);
        assertTableNotPresent(TABLE_NAME);

        List<Map<String, Object>> results = results("SELECT * FROM " + newName);
        assertEquals("Data not retained after rename: " + results, 1, results.size());
        assertEquals("Data not retained after rename: " + results, 1, results.get(0).get("ID"));
        assertEquals("Data not retained after rename: " + results, "Bering is Groovy", results.get(0).get("TITLE"));
    }

    public void testAddColumn() throws Exception {
        adapter.addColumn(TABLE_NAME, createColumn("author_id", Types.INTEGER));

        stmt.execute("INSERT INTO " + TABLE_NAME + " (id, title, author_id) VALUES (2, 'Groovy for Bering', 14)");
        // expect no error

        ResultSet rs = stmt.executeQuery("SELECT author_id FROM " + TABLE_NAME + " WHERE id=2");
        int count = 0;
        while (rs.next()) {
            count++;
            assertEquals(14, rs.getObject("author_id"));
        }
        assertEquals("Wrong number of results", 1, count);
    }

    public void testRemoveColumn() throws Exception {
        adapter.dropColumn(TABLE_NAME, "title");

        ResultSet rs = stmt.executeQuery("SELECT * FROM " + TABLE_NAME);
        int count = 0;
        while (rs.next()) {
            count++;
            assertEquals(1, rs.getInt("id"));
            try {
                rs.getString("title");
                fail("No exception for missing column");
            } catch (Exception e) {
                // expected
                // e.printStackTrace();
            }
        }
        assertEquals(1, count);
    }

    public void testSetDefault() throws Exception {
        {
            String newDefault = "Foo";
            adapter.setDefaultValue(TABLE_NAME, "title", newDefault);
            stmt.execute("INSERT INTO " + TABLE_NAME + " (id) VALUES (2)");

            List<Map<String, Object>> actual = results("SELECT * FROM " + TABLE_NAME + " where id=2");
            assertEquals("Wrong number of results for query", 1, actual.size());
            assertEquals("Default value not assigned to undefined column: " + actual.get(0),
                newDefault, actual.get(0).get("TITLE"));
        }

        {
            adapter.setDefaultValue(TABLE_NAME, "title", null);
            stmt.execute("INSERT INTO " + TABLE_NAME + " (id) VALUES (3)");
            List<Map<String, Object>> actual = results("SELECT * FROM " + TABLE_NAME + " where id=3");
            assertEquals("Wrong number of results for query", 1, actual.size());
            assertEquals("Default value not cleared: " + actual.get(0),
                null, actual.get(0).get("TITLE"));
        }
    }

    public void testSetNullable() throws Exception {
        stmt.execute(String.format("ALTER TABLE %s ALTER COLUMN %s VARCHAR NOT NULL", TABLE_NAME, "title"));

        adapter.setNullable(TABLE_NAME, "title", true);

        stmt.execute(String.format("INSERT INTO %s (title) VALUES (NULL)", TABLE_NAME));
        // expect no exception
    }

    public void testSetNotNullable() throws Exception {
        adapter.setNullable(TABLE_NAME, "title", false);

        try {
            stmt.execute(String.format("INSERT INTO %s (title) VALUES (NULL)", TABLE_NAME));
            fail("Exception not thrown");
        } catch (SQLException e) {
            // expected
        }
    }

    public void testInsert() throws Exception {
        String newTitle = "Bering is Groovy 2nd Ed";
        adapter.insert(TABLE_NAME, Arrays.asList("title"), Arrays.asList((Object) newTitle), true);

        assertEquals(1, results("SELECT * FROM " + TABLE_NAME + " WHERE title='" + newTitle + '\'').size());
    }

    public void testLoadVersionTableWithNoTable() throws Exception {
        Version actual = adapter.loadVersions();
        assertEquals(0, actual.getReleaseNumbers().size());

        assertTablePresent(VERSION_TABLE_NAME);
    }

    public void testLoadVersionTable() throws Exception {
        stmt.execute("CREATE TABLE " + VERSION_TABLE_NAME + " (release INTEGER NOT NULL, migration INTEGER NOT NULL)");
        stmt.execute("INSERT INTO " + VERSION_TABLE_NAME
            + " (release, migration) VALUES (1, 5)");
        stmt.execute("INSERT INTO " + VERSION_TABLE_NAME
            + " (release, migration) VALUES (2, 2)");
        stmt.execute("INSERT INTO " + VERSION_TABLE_NAME
            + " (release, migration) VALUES (4, 6)");
        stmt.execute("INSERT INTO " + VERSION_TABLE_NAME
            + " (release, migration) VALUES (7, 1)");

        Version actual = adapter.loadVersions();
        assertEquals("Wrong number of versions found", 4, actual.getReleaseNumbers().size());
        assertEquals(5, (int) actual.getMigrationNumber(1));
        assertEquals(2, (int) actual.getMigrationNumber(2));
        assertEquals(6, (int) actual.getMigrationNumber(4));
        assertEquals(1, (int) actual.getMigrationNumber(7));
    }

    public void testUpdateVersionTableWithNoPreviousVersion() throws Exception {
        stmt.execute("CREATE TABLE " + VERSION_TABLE_NAME + " (release INTEGER NOT NULL, migration INTEGER NOT NULL)");

        adapter.updateVersion(2, 4);
        ResultSet rs = stmt.executeQuery("SELECT release, migration FROM " + VERSION_TABLE_NAME + " WHERE release=2");
        boolean any = false;
        while (rs.next()) {
            any = true;
            assertEquals(2, rs.getInt("release"));
            assertEquals(4, rs.getInt("migration"));
        }
        assertTrue("Result not stored", any);
    }

    public void testUpdateVersionTableWithPreviousVersion() throws Exception {
        stmt.execute("CREATE TABLE " + VERSION_TABLE_NAME + " (release INTEGER NOT NULL, migration INTEGER NOT NULL)");
        stmt.execute("INSERT INTO " + VERSION_TABLE_NAME + "(release, migration) VALUES (3, 7)");

        adapter.updateVersion(3, 2);
        ResultSet rs = stmt.executeQuery("SELECT release, migration FROM " + VERSION_TABLE_NAME + " WHERE release=3");
        boolean any = false;
        while (rs.next()) {
            any = true;
            assertEquals(3, rs.getInt("release"));
            assertEquals(2, rs.getInt("migration"));
        }
        assertTrue("Result not present", any);
    }

    public void testUpdateVersionTableToMigrationZero() throws Exception {
        stmt.execute("CREATE TABLE " + VERSION_TABLE_NAME + " (release INTEGER NOT NULL, migration INTEGER NOT NULL)");
        stmt.execute("INSERT INTO " + VERSION_TABLE_NAME + "(release, migration) VALUES (3, 7)");

        adapter.updateVersion(3, 0);
        ResultSet rs = stmt.executeQuery("SELECT release, migration FROM " + VERSION_TABLE_NAME + " WHERE release=3");
        boolean any = false;
        while (rs.next()) {
            any = true;
        }
        assertFalse("Should be no results", any);
    }

    public void testExecute() throws Exception {
        adapter.execute("UPDATE " + TABLE_NAME + " SET title='New Title'");

        ResultSet rs = stmt.executeQuery("SELECT title FROM " + TABLE_NAME);
        boolean any = false;
        while (rs.next()) {
            assertEquals("New Title", rs.getString("title"));
            any = true;
        }
        assertTrue("Malformed test -- expected rows in " + TABLE_NAME, any);
    }

    public void testDialectGuessedIfNull() throws Exception {
        DatabaseAdapter newAdapter = new DatabaseAdapter(conn, null);
        assertEquals(Hsqldb.class, newAdapter.getDialect().getClass());
    }

    private void assertTablePresent(String tableName) throws SQLException {
        stmt.execute("SELECT * FROM " + tableName);
        // expect no error
    }

    private void assertTableNotPresent(String tableName) {
        try {
            stmt.execute("SELECT * FROM " + tableName);
            fail("No exception for non-existent table");
        } catch (Exception e) {
            // e.printStackTrace();
        }
    }

    private List<Map<String, Object>> results(String sql) throws SQLException {
        return results(stmt.executeQuery(sql));
    }

    private List<Map<String, Object>> results(ResultSet rs) throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData();
        int count = rsmd.getColumnCount();
        List<Map<String, Object>> results = new LinkedList<Map<String, Object>>();
        while (rs.next()) {
            Map<String, Object> row = new LinkedHashMap<String, Object>();
            for (int i = 1 ; i <= count ; i++) {
                row.put(rsmd.getColumnName(i), rs.getObject(i));
            }
            results.add(row);
        }
        return results;
    }

    private Column createColumn(String name, int type) {
        Column newCol = new Column();
        newCol.setName(name);
        newCol.setTypeCode(type);
        return newCol;
    }
}
