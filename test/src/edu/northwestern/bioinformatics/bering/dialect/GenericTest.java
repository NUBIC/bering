package edu.northwestern.bioinformatics.bering.dialect;

import edu.northwestern.bioinformatics.bering.TableDefinition;
import static edu.northwestern.bioinformatics.bering.dialect.DdlUtilsTools.*;
import static org.easymock.classextension.EasyMock.expect;

import java.util.List;

/**
 * @author Rhett Sutphin
 */
public class GenericTest extends DdlUtilsDialectTestCase<Generic> {
    protected Generic createDialect() { return new Generic(); }

    public void testCreateTable() throws Exception {
        TableDefinition def = createTestTable();
        String expectedSql = "CREATE TABLE etc";

        expect(getPlatform().getCreateTablesSql(createDatabase(def.toTable()), false, false))
            .andReturn(expectedSql);
        replayMocks();

        List<String> statements = getDialect().createTable(def);
        verifyMocks();

        assertStatements(statements, expectedSql);
    }

    public void testDropTable() throws Exception {
        String tableName = "test";
        String expectedSql = "DROP TABLE";

        expect(getPlatform().getDropTablesSql(createDatabase(createIdedTable(tableName)), false))
            .andReturn(expectedSql);
        replayMocks();

        List<String> statements = getDialect().dropTable(tableName);
        verifyMocks();

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

    private static TableDefinition createTestTable() {
        TableDefinition def = new TableDefinition("feast");
        def.addColumn("name", "string");
        def.addColumn("length", "integer");
        return def;
    }
}
