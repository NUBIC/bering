package edu.northwestern.bioinformatics.bering.dialect;

import edu.northwestern.bioinformatics.bering.TableDefinition;
import edu.northwestern.bioinformatics.bering.Column;

import java.util.Collections;

/**
 * This test covers elements of the abstract {@link HibernateBasedDialect} class
 * that are shared across all dialects.  It uses the PostgreSQL dialect for a
 * concrete implementation, but should not test anything PostgreSQL-specific.
 *
 * @author Rhett Sutphin
 */
public class HibernateBasedDialectTest extends DialectTestCase<PostgreSQL> {
    protected Class<PostgreSQL> getDialectClass() {
        return PostgreSQL.class;
    }

    public void testCreateTableWithForeignKey() throws Exception {
        TableDefinition table = new TableDefinition("feasts");
        table.setIncludePrimaryKey(false);
        table.addColumn(
            Collections.<String, Object>singletonMap("references", "rooms"), "room_id", "integer");
        assertStatements(
            getDialect().createTable(table),
            "CREATE TABLE feasts (\n  room_id INT4,\n  CONSTRAINT fk_feasts_rooms FOREIGN KEY (room_id) REFERENCES rooms(id)\n)"
        );
    }
    
    public void testAddColumnWithForeignKey() throws Exception {
        assertStatements(
            getDialect().addColumn("feasts", Column.createColumn(
                Collections.<String, Object>singletonMap("references", "rooms"), "room_id", "integer")),
            "ALTER TABLE feasts ADD COLUMN room_id INT4",
            "ALTER TABLE feasts ADD CONSTRAINT fk_feasts_rooms FOREIGN KEY (room_id) REFERENCES rooms(id)"
        );
    }
}
