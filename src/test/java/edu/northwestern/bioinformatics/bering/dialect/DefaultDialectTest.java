package edu.northwestern.bioinformatics.bering.dialect;

import edu.northwestern.bioinformatics.bering.Column;
import edu.northwestern.bioinformatics.bering.TableDefinition;

import java.util.Arrays;
import java.util.List;

/**
 * This test covers shared behaviors in AbstractDialect.
 *
 * @author Rhett Sutphin
 */
public class DefaultDialectTest extends DialectTestCase<DefaultDialectTest.TestDialect> {
    @Override
    protected Class<TestDialect> getDialectClass() {
        return TestDialect.class;
    }

    public void testRenameTable() throws Exception {
        String tableName = "test";
        String newTableName = "t_test";
        assertStatements(
            getDialect().renameTable(tableName, newTableName, true),
            "ALTER TABLE " + tableName + " RENAME TO " + newTableName);
    }

    public void testDropTable() throws Exception {
        String tableName = "test";
        String expectedSql = "DROP TABLE test";

        List<String> statements = getDialect().dropTable(tableName, true);
        assertStatements(statements, expectedSql);
    }

    public void testDropNoPkTable() throws Exception {
        String tableName = "test";
        String expectedSql = "DROP TABLE test";

        List<String> statements = getDialect().dropTable(tableName, false);
        assertStatements(statements, expectedSql);
    }

    public void testSetDefaultValue() throws Exception {
        List<String> statements = getDialect().setDefaultValue("feast", "length", "8");
        assertStatements(statements, "ALTER TABLE feast ALTER COLUMN length SET DEFAULT '8'");
    }

    public void testUnsetDefaultValue() throws Exception {
        List<String> statements = getDialect().setDefaultValue("feast", "length", null);
        assertStatements(statements, "ALTER TABLE feast ALTER COLUMN length SET DEFAULT NULL");
    }

    public void testSetNullable() throws Exception {
        List<String> statements = getDialect().setNullable("feast", "length", true);
        assertStatements(statements, "ALTER TABLE feast ALTER COLUMN length SET NULL");
    }

    public void testSetNotNullable() throws Exception {
        List<String> statements = getDialect().setNullable("feast", "length", false);
        assertStatements(statements, "ALTER TABLE feast ALTER COLUMN length SET NOT NULL");
    }

    public void testRenameColumn() throws Exception {
        List<String> statements = getDialect().renameColumn("feast", "length", "duration");
        assertStatements(statements, "ALTER TABLE feast RENAME COLUMN length TO duration");
    }

    public void testDropColumn() throws Exception {
        List<String> statements = getDialect().dropColumn("feast", "length");
        assertStatements(statements, "ALTER TABLE feast DROP COLUMN length");
    }

    public void testSeparateStatements() throws Exception {
        String script =
            "CREATE TABLE etc;\n" +
            "CREATE SEQUENCE etc;\n" +
            "INSERT INTO etc (alia) VALUES ('foo');";
        assertStatements(
            getDialect().separateStatements(script),
            "CREATE TABLE etc",
            "CREATE SEQUENCE etc",
            "INSERT INTO etc (alia) VALUES ('foo')"
        );
    }

    public void testInsert() throws Exception {
        assertStatements(
            getDialect().insert("feast", Arrays.asList("length", "cost"), Arrays.asList((Object) "An hour", 100), true),
            "INSERT INTO feast (length, cost) VALUES ('An hour', 100)"
        );
    }

    public static class TestDialect extends AbstractDialect {
        public String getDialectName() {
            return "test";
        }

        public List<String> createTable(TableDefinition table) {
            throw new UnsupportedOperationException("createTable not implemented");
        }

        public List<String> addColumn(String tableName, Column column) {
            throw new UnsupportedOperationException("addColumn not implemented");
        }
    }
}
