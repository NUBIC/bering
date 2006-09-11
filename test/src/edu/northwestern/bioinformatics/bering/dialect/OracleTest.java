package edu.northwestern.bioinformatics.bering.dialect;

import edu.northwestern.bioinformatics.bering.TableDefinition;
import org.apache.ddlutils.model.Database;
import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.*;

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
        expect(getPlatformInfo().getMaxIdentifierLength()).andReturn(15);
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
        expect(getPlatformInfo().getMaxIdentifierLength()).andReturn(15);
        expect(getPlatform().getCreateTablesSql((Database) notNull(), eq(false), eq(false)))
            .andReturn(expectedCreateTable);
        replayMocks();

        TableDefinition def = new TableDefinition("bering_version", false);
        def.addColumn("release", "integer");
        assertStatements(
            getDialect().createTable(def.toTable()),
            expectedCreateTable
        );
        verifyMocks();
    }

    public void testCreateTableWithLongName() throws Exception {
        String expectedCreateTable = "CREATE TABLE etc";
        expect(getPlatformInfo().getMaxIdentifierLength()).andReturn(15);
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

    public void testDropTable() throws Exception {
        String expectedDropTable = "CREATE TABLE etc";
        expect(getPlatformInfo().getMaxIdentifierLength()).andReturn(15);
        expect(getPlatform().getDropTablesSql((Database) notNull(), eq(false)))
            .andReturn(expectedDropTable);
        replayMocks();

        TableDefinition def = new TableDefinition("feast");
        def.addColumn("length", "integer");
        assertStatements(
            getDialect().dropTable(def.toTable()),
            expectedDropTable,
            "DROP SEQUENCE feast_id_seq"
        );
        verifyMocks();
    }
}
