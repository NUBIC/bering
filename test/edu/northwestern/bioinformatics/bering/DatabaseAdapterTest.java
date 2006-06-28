package edu.northwestern.bioinformatics.bering;

import junit.framework.TestCase;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.apache.ddlutils.model.Column;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.Types;
import java.sql.SQLException;
import java.sql.ResultSet;

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
        adapter = new DatabaseAdapter(new SingleConnectionDataSource(conn, true));
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
                e.printStackTrace();
            }
        }
        assertEquals(1, count);
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
            e.printStackTrace();
        }
    }

    private Column createColumn(String name, int type) {
        Column newCol = new Column();
        newCol.setName(name);
        newCol.setTypeCode(type);
        return newCol;
    }
}
