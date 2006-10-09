package edu.northwestern.bioinformatics.bering.dialect;

import edu.northwestern.bioinformatics.bering.TableDefinition;
import edu.northwestern.bioinformatics.bering.Migration;
import org.apache.ddlutils.model.Database;
import org.apache.ddlutils.model.Table;
import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.*;

import java.util.Arrays;
import java.util.List;
import java.util.Collections;

/**
 * @author Rhett Sutphin
 */
public class OracleTest extends DdlUtilsDialectTestCase<Oracle> {
    protected Oracle createDialect() { return new Oracle(); }

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
        String expectedCreateTable = "CREATE TABLE etc";
        expectGetMaxIdentLength(15);
        expect(getPlatform().getCreateTablesSql((Database) notNull(), eq(false), eq(false)))
            .andReturn(expectedCreateTable);
        replayMocks();

        TableDefinition def = new TableDefinition("feast");
        def.addColumn("length", "integer");
        assertStatements(
            getDialect().createTable(def.toTable()),
            "CREATE SEQUENCE seq_feast_id",
            expectedCreateTable
        );
        verifyMocks();
    }

    public void testCreateTableWithoutPK() throws Exception {
        String expectedCreateTable = "CREATE TABLE etc";
        expect(getPlatform().getCreateTablesSql((Database) notNull(), eq(false), eq(false)))
            .andReturn(expectedCreateTable);
        replayMocks();

        TableDefinition def = new TableDefinition("bering_version");
        def.setIncludePrimaryKey(false);
        def.addColumn("release", "integer");
        assertStatements(
            getDialect().createTable(def.toTable()),
            expectedCreateTable
        );
        verifyMocks();
    }

    public void testCreateTableWithManualPKDoesNotCreateSequence() throws Exception {
        String expectedCreateTable = "CREATE TABLE etc";
        expect(getPlatform().getCreateTablesSql((Database) notNull(), eq(false), eq(false)))
            .andReturn(expectedCreateTable);
        replayMocks();

        TableDefinition def = new TableDefinition("feast");
        def.setIncludePrimaryKey(false);
        def.addColumn(Collections.singletonMap(Migration.PRIMARY_KEY_KEY, (Object) Boolean.TRUE), "id", "integer");
        def.addColumn("length", "integer");
        assertStatements(
            getDialect().createTable(def.toTable()),
            expectedCreateTable
        );
        verifyMocks();
    }

    public void testCreateTableWithLongName() throws Exception {
        String expectedCreateTable = "CREATE TABLE etc";
        expectGetMaxIdentLength(15);
        expect(getPlatform().getCreateTablesSql((Database) notNull(), eq(false), eq(false)))
            .andReturn(expectedCreateTable);
        replayMocks();

        TableDefinition def = new TableDefinition("superfeast2000");
        def.addColumn("length", "integer");
        assertStatements(
            getDialect().createTable(def.toTable()),
            "CREATE SEQUENCE seq_superfea_id",
            expectedCreateTable
        );
        verifyMocks();
    }

    private void expectGetMaxIdentLength(int maxlen) {
        expect(getPlatformInfo().getMaxIdentifierLength()).andReturn(maxlen);
    }

    public void testDropTable() throws Exception {
        String expectedDropTable = "CREATE TABLE etc";
        expectGetMaxIdentLength(15);
        expect(getPlatform().getDropTablesSql((Database) notNull(), eq(false)))
            .andReturn(expectedDropTable);
        replayMocks();

        TableDefinition def = new TableDefinition("feast");
        def.addColumn("length", "integer");
        assertStatements(
            getDialect().dropTable(def.toTable()),
            expectedDropTable,
            "DROP SEQUENCE seq_feast_id"
        );
        verifyMocks();
    }

    public void testRenameTableWithPk() throws Exception {
        String tableName = "test";
        String newTableName = "t_test";
        expectGetMaxIdentLength(15);
        expectGetMaxIdentLength(15);
        replayMocks();

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
        expectGetMaxIdentLength(30);
        replayMocks();
        assertStatements(
            getDialect().insert("feast", Arrays.asList("length", "cost"), Arrays.asList((Object) "An hour", 100), true),
            "INSERT INTO feast (id, length, cost) VALUES (seq_feast_id.nextval, 'An hour', 100)"
        );
        verifyMocks();
    }

    public void testInsertNoPrimaryKey() throws Exception {
        replayMocks();
        assertStatements(
            getDialect().insert("feast", Arrays.asList("length", "cost"), Arrays.asList((Object) "An hour", 100), false),
            "INSERT INTO feast (length, cost) VALUES ('An hour', 100)"
        );
        verifyMocks();
    }

    public void testMassageTableRemovesAutoIncrement() throws Exception {
        TableDefinition def = new TableDefinition("feast");
        def.addColumn("length", "integer");
        Table massaged = Oracle.massageTableForOracle(def.toTable());
        assertFalse("PK still autoincrement", massaged.getPrimaryKeyColumns()[0].isAutoIncrement());
    }
}
