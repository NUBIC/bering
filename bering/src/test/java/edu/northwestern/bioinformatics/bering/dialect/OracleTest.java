package edu.northwestern.bioinformatics.bering.dialect;

import edu.northwestern.bioinformatics.bering.Migration;
import edu.northwestern.bioinformatics.bering.TableDefinition;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Rhett Sutphin
 */
public class OracleTest extends HibernateBasedDialectTestCase<Oracle> {

    @Override
    protected Class<Oracle> getDialectClass() {
        return Oracle.class;
    }

    public void testName() throws Exception {
        assertEquals("oracle", getDialect().getDialectName());
    }

    public void testSetDefault() throws Exception {
        assertStatements(getDialect().setDefaultValue("feast", "length", "8"),
            "ALTER TABLE feast MODIFY (length DEFAULT '8')"
        );
    }

    public void testUnsetDefault() throws Exception {
        assertStatements(getDialect().setDefaultValue("feast", "length", null),
            "ALTER TABLE feast MODIFY (length DEFAULT NULL)"
        );
    }

    public void testSetNullable() throws Exception {
        assertStatements(
            getDialect().setNullable("feast", "length", true),
            "ALTER TABLE feast MODIFY (length NULL)"
        );
    }

    public void testSetNotNullable() throws Exception {
        assertStatements(
            getDialect().setNullable("feast", "length", false),
            "ALTER TABLE feast MODIFY (length NOT NULL)"
        );
    }

    public void testCreateTableIncludesSequence() throws Exception {
        String expectedCreateTable
            = "CREATE TABLE feast (\n  id NUMBER(10,0),\n  name VARCHAR2(2000 CHAR),\n  length NUMBER(10,0),\n  PRIMARY KEY(id)\n)";

        assertStatements(
            getDialect().createTable(createTestTable()),
            "CREATE SEQUENCE seq_feast_id",
            expectedCreateTable
        );
    }

    public void testCreateTableWithoutPK() throws Exception {
        String expectedCreateTable = "CREATE TABLE feast (\n  name VARCHAR2(2000 CHAR),\n  length NUMBER(10,0)\n)";

        TableDefinition def = createTestTable();
        def.setIncludePrimaryKey(false);
        assertStatements(
            getDialect().createTable(def),
            expectedCreateTable
        );
    }

    public void testCreateTableWithManualPKDoesNotCreateSequence() throws Exception {
        String expectedCreateTable = "CREATE TABLE feast (\n  name VARCHAR2(2000 CHAR),\n  length NUMBER(10,0),\n  id NUMBER(10,0) PRIMARY KEY\n)";
        TableDefinition def = createTestTable();
        def.setIncludePrimaryKey(false);
        def.addColumn(Collections.singletonMap(Migration.PRIMARY_KEY_KEY, (Object) Boolean.TRUE), "id", "integer");
        assertStatements(
            getDialect().createTable(def),
            expectedCreateTable
        );
    }

    public void testCreateTableWithLongName() throws Exception {
        String expectedCreateTable
            = "CREATE TABLE superfeast01234567890123456789 (\n  id NUMBER(10,0),\n  length NUMBER(10,0),\n  PRIMARY KEY(id)\n)";

        TableDefinition def = new TableDefinition("superfeast01234567890123456789");
        def.addColumn("length", "integer");
        assertStatements(
            getDialect().createTable(def),
            "CREATE SEQUENCE seq_superfeast0123456789012_id",
            expectedCreateTable
        );
        verifyMocks();
    }

    public void testDropTable() throws Exception {
        String expectedDropTable = "DROP TABLE feast";
        String tableName = "feast";

        assertStatements(
            getDialect().dropTable(tableName, true),
            expectedDropTable,
            "DROP SEQUENCE seq_feast_id"
        );
    }


    public void testRenameTableWithPk() throws Exception {
        String tableName = "test";
        String newTableName = "t_test";
        assertStatements(
            getDialect().renameTable(tableName, newTableName, true),
            "ALTER TABLE test RENAME TO t_test",
            "RENAME seq_test_id TO seq_t_test_id"
        );
    }

    public void testRenameTableWithoutPk() throws Exception {
        String tableName = "test";
        String newTableName = "t_test";

        assertStatements(
            getDialect().renameTable(tableName, newTableName, false),
            "ALTER TABLE test RENAME TO t_test"
        );
    }

    public void testSeparateStatementsNoPlSql() throws Exception {
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

    public void testSeparateStatementsPlSqlOnly() throws Exception {
        List<String> expectedStmts = Arrays.asList(
            "CREATE TRIGGER trg_bar_id " +
            "BEFORE INSERT\n" +
            "ON bar\n" +
            "FOR EACH ROW\n" +
            "BEGIN\n" +
            "  SELECT BAR_SEQ.NEXTVAL\n" +
            "  INTO :NEW.ID\n" +
            "  FROM DUAL;\n" +
            "END;",
            "CREATE TRIGGER trg_foo_id " +
            "BEFORE INSERT\n" +
            "ON foo\n" +
            "FOR EACH ROW\n" +
            "BEGIN\n" +
            "  SELECT FOO_SEQ.NEXTVAL\n" +
            "  INTO :NEW.ID\n" +
            "  FROM DUAL;\n" +
            "END;"
        );
        String script = expectedStmts.get(0) + "\n/\n;\n" + expectedStmts.get(1) + "\n/\n;";

        assertStatements(
            getDialect().separateStatements(script),
            expectedStmts.toArray(new String[expectedStmts.size()])
        );
    }

    public void testSeparateStatements() throws Exception {
        List<String> expectedStmts = Arrays.asList(
            "CREATE TABLE bar",
            "CREATE SEQUENCE BAR_SEQ",
            "CREATE TRIGGER trg_bar_id BEFORE INSERT ON bar FOR EACH ROW\n" +
            "BEGIN\n" +
            "  SELECT BAR_SEQ.NEXTVAL\n" +
            "  INTO :NEW.ID\n" +
            "  FROM DUAL;\n" +
            "END",
            "CREATE TABLE foo",
            "CREATE SEQUENCE FOO_SEQ",
            "CREATE TRIGGER trg_foo_id " +
            "BEFORE INSERT\n" +
            "ON foo\n" +
            "FOR EACH ROW\n" +
            "BEGIN\n" +
            "  SELECT FOO_SEQ.NEXTVAL\n" +
            "  INTO :NEW.ID\n" +
            "  FROM DUAL;\n" +
            "END",
            "INSERT INTO foo (etc) VALUES (hmm)"
        );
        String script =
            expectedStmts.get(0) + ";\n" +
            expectedStmts.get(1) + ";\n" +
            expectedStmts.get(2) + "\n/\r;\n" +
            expectedStmts.get(3) + ";\n" +
            expectedStmts.get(4) + ";\n" +
            expectedStmts.get(5) + "\r\n/\n;\n" +
            expectedStmts.get(6) + ";\n";

        assertStatements(
            getDialect().separateStatements(script),
            expectedStmts.toArray(new String[expectedStmts.size()])
        );
    }

    public void testSeparateStatementsIgnoresEmbeddedSlashes() throws Exception {
        String script = "ABC/DEF; GHI";
        assertStatements(
            getDialect().separateStatements(script),
            "ABC/DEF",
            "GHI"
        );
    }

    public void testInsert() throws Exception {
        assertStatements(
            getDialect().insert("feast", Arrays.asList("length", "cost"), Arrays.asList((Object) "An hour", 100), true),
            "INSERT INTO feast (id, length, cost) VALUES (seq_feast_id.nextval, 'An hour', 100)"
        );
    }

    public void testInsertNoPrimaryKey() throws Exception {
        assertStatements(
            getDialect().insert("feast", Arrays.asList("length", "cost"), Arrays.asList((Object) "An hour", 100), false),
            "INSERT INTO feast (length, cost) VALUES ('An hour', 100)"
        );
    }

    @Override
    protected String expectedAddStringStatement() {
        return "ALTER TABLE t ADD (c VARCHAR2(2000 CHAR))";
    }

    @Override
    protected String expectedAddIntegerStatement() {
        return "ALTER TABLE t ADD (c NUMBER(10,0))";
    }

    @Override
    protected String expectedAddFloatStatement() {
        return "ALTER TABLE t ADD (c FLOAT)";
    }

    @Override
    protected String expectedAddBooleanStatement() {
        return "ALTER TABLE t ADD (c NUMBER(1,0))";
    }

    @Override
    protected String expectedAddDateStatement() {
        return "ALTER TABLE t ADD (c DATE)";
    }

    @Override
    protected String expectedAddTimeStatement() {
        return "ALTER TABLE t ADD (c DATE)";
    }

    @Override
    protected String expectedAddTimestampStatement() {
        return "ALTER TABLE t ADD (c TIMESTAMP)";
    }
}