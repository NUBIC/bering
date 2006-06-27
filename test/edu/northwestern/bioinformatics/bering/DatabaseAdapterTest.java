package edu.northwestern.bioinformatics.bering;

import junit.framework.TestCase;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

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
        try {
            stmt.execute("SELECT * FROM books");
            fail("No exception for non-existent table");
        } catch (Exception e) {
            e.printStackTrace();
        }

        TableDefinition def = new TableDefinition("books", adapter);
        def.addColumn("title", "string");
        def.addColumn("published", "timestamp");
        adapter.createTable(def);

        stmt.execute("SELECT * FROM books");
        // expect no error
    }
}
