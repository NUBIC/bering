package edu.northwestern.bioinformatics.bering;

import static edu.northwestern.bioinformatics.bering.DatabaseAdapter.VERSION_TABLE_NAME;
import edu.northwestern.bioinformatics.bering.runtime.Version;
import junit.framework.TestCase;
import org.apache.ddlutils.model.Column;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

/**
 * @author rsutphin
 */
public class DatabaseAdapterTest extends TestCase {
    private static final String TABLE_NAME = "books";

    private Adapter adapter;
    private Connection conn;
    private Statement stmt;

    protected void setUp() throws Exception {
        super.setUp();
        Class.forName("org.hsqldb.jdbcDriver");
        // in-memory databases apparently persist across connections within the same JVM
        // hence the random number added to the name
        conn = DriverManager.getConnection("jdbc:hsqldb:mem:test" + Math.random(), "sa", "");
        conn.setAutoCommit(false);
        adapter = new DatabaseAdapter(conn);
        stmt = conn.createStatement();

        stmt.execute("CREATE TABLE " + TABLE_NAME + " (id INTEGER PRIMARY KEY, title VARCHAR(50))");
        stmt.execute("INSERT INTO " + TABLE_NAME + " (id, title) VALUES (1, 'Bering is Groovy')");

        assertTablePresent(TABLE_NAME);
    }

    protected void tearDown() throws Exception {
        conn.close();
        super.tearDown();
    }

    public void testCreateTable() throws Exception {
        String tableName = "authors";
        assertTableNotPresent(tableName);

        TableDefinition def = new TableDefinition(tableName, new StubMigration());
        def.addColumn("name", "string");
        def.addColumn("birthdate", "timestamp");
        adapter.createTable(def);

        assertTablePresent(tableName);
        stmt.execute("INSERT INTO " + tableName + "(id, name, birthdate) VALUES (14, 'Dave', '2006-04-15')");
        // expect no error
    }

    public void testDropTable() throws Exception {
        assertTablePresent(TABLE_NAME);

        adapter.dropTable(TABLE_NAME);
        assertTableNotPresent(TABLE_NAME);
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
        adapter.removeColumn(TABLE_NAME, "title");

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

    private Column createColumn(String name, int type) {
        Column newCol = new Column();
        newCol.setName(name);
        newCol.setTypeCode(type);
        return newCol;
    }
}
