package edu.northwestern.bioinformatics.bering;

import junit.framework.TestCase;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.Types;
import java.sql.SQLException;

/**
 * @author rsutphin
 */
public class DatabaseAdapterTest extends TestCase {
    private DatabaseAdapter adapter;
    private Connection conn;
    private Statement stmt;

    protected void setUp() throws Exception {
        super.setUp();
        Class.forName("org.hsqldb.jdbcDriver");
        conn = DriverManager.getConnection("jdbc:hsqldb:mem:test" + Math.random(), "sa", "");
        adapter = new DatabaseAdapter(new SingleConnectionDataSource(conn, true));
        stmt = conn.createStatement();
    }

    protected void tearDown() throws Exception {
        conn.close();
        super.tearDown();
    }

    public void testCreateTable() throws Exception {
        String tableName = "books";
        assertTableNotPresent(tableName);

        TableDefinition def = new TableDefinition(tableName, adapter);
        def.addColumn("title", "string");
        def.addColumn("published", "timestamp");
        adapter.createTable(def);

        assertTablePresent(tableName);
    }
    
    public void testDropTable() throws Exception {
        String tableName = "books";
        stmt.execute("CREATE TABLE " + tableName + "(id INTEGER PRIMARY KEY)");
        assertTablePresent(tableName);

        adapter.dropTable(tableName);
        assertTableNotPresent(tableName);
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

    public void testTypes() throws Exception {
        assertEquals(Types.BOOLEAN,   adapter.getTypeCode("boolean"));
        assertEquals(Types.DATE,      adapter.getTypeCode("date"));
        assertEquals(Types.TIME,      adapter.getTypeCode("time"));
        assertEquals(Types.TIMESTAMP, adapter.getTypeCode("timestamp"));
        assertEquals(Types.VARCHAR,   adapter.getTypeCode("string"));
        assertEquals(Types.NUMERIC,   adapter.getTypeCode("float"));
        assertEquals(Types.INTEGER,   adapter.getTypeCode("integer"));
    }

    public void testInvalidTypeThrowsException() throws Exception {
        try {
            adapter.getTypeCode("fancytype");
            fail("No exception thrown");
        } catch (IllegalArgumentException iae) {
            assertTrue(iae.getMessage().indexOf("fancytype") >= 0);
        }
    }
}
