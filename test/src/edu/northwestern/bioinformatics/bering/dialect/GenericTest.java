package edu.northwestern.bioinformatics.bering.dialect;

import edu.northwestern.bioinformatics.bering.BeringTestCase;
import edu.northwestern.bioinformatics.bering.TableDefinition;
import static edu.northwestern.bioinformatics.bering.dialect.DdlUtilsTools.*;
import org.apache.ddlutils.Platform;
import static org.easymock.classextension.EasyMock.expect;

import java.util.List;

/**
 * @author Rhett Sutphin
 */
public class GenericTest extends BeringTestCase {
    private Platform platform;
    private Generic dialect;

    protected void setUp() throws Exception {
        super.setUp();
        dialect = new Generic();
        platform = registerMockFor(Platform.class);
        dialect.setPlatform(platform);
    }

    public void testCreateTable() throws Exception {
        TableDefinition def = createTestTable();
        String expectedSql = "CREATE TABLE etc";

        expect(platform.getCreateTablesSql(createDatabase(def.toTable()), false, false))
            .andReturn(expectedSql);
        replayMocks();

        List<String> statements = dialect.createTable(def);
        verifyMocks();

        assertStatements(statements, expectedSql);
    }

    public void testDropTable() throws Exception {
        String tableName = "test";
        String expectedSql = "DROP TABLE";

        expect(platform.getDropTablesSql(createDatabase(createIdedTable(tableName)), false))
            .andReturn(expectedSql);
        replayMocks();

        List<String> statements = dialect.dropTable(tableName);
        verifyMocks();

        assertStatements(statements, expectedSql);
    }

    public void testSetDefaultValue() throws Exception {
        List<String> statements = dialect.setDefaultValue("feast", "length", "8");
        assertStatements(statements, "ALTER TABLE feast ALTER COLUMN length SET DEFAULT '8'");
    }

    public void testUnsetDefaultValue() throws Exception {
        List<String> statements = dialect.setDefaultValue("feast", "length", null);
        assertStatements(statements, "ALTER TABLE feast ALTER COLUMN length SET DEFAULT NULL");
    }

    private static void assertStatements(List<String> actual, String... expected) {
        assertEquals("Wrong number of statements: " + actual, expected.length, actual.size());
        for (int i = 0; i < expected.length; i++) {
            assertEquals("Wrong statment " + i, expected[0], actual.get(0));
        }
    }

    private static TableDefinition createTestTable() {
        TableDefinition def = new TableDefinition("feast");
        def.addColumn("name", "string");
        def.addColumn("length", "integer");
        return def;
    }
}
