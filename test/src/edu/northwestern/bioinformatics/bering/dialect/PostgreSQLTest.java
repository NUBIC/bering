package edu.northwestern.bioinformatics.bering.dialect;

import org.apache.ddlutils.model.Column;
import org.apache.ddlutils.alteration.AddColumnChange;
import org.apache.ddlutils.alteration.ColumnChange;
import org.easymock.classextension.EasyMock;
import static org.easymock.classextension.EasyMock.*;
import org.easymock.IArgumentMatcher;

import java.util.List;

/**
 * @author Rhett Sutphin
 */
public class PostgreSQLTest extends DdlUtilsDialectTestCase<PostgreSQL> {
    protected PostgreSQL createDialect() { return new PostgreSQL(); }

    public void testSetNullable() throws Exception {
        assertStatements(
            getDialect().setNullable("feast", "length", true),
            "ALTER TABLE feast ALTER COLUMN length DROP NOT NULL"
        );
    }

    public void testSetNotNullable() throws Exception {
        assertStatements(
            getDialect().setNullable("feast", "length", false),
            "ALTER TABLE feast ALTER COLUMN length SET NOT NULL"
        );
    }

    public void testRenameTableWithPk() throws Exception {
        String tableName = "test";
        String newTableName = "t_test";

        assertStatements(
            getDialect().renameTable(tableName, newTableName, true),
            "ALTER TABLE test RENAME TO t_test",
            "ALTER TABLE test_id_seq RENAME TO t_test_id_seq"
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
    

    public void testAddDefaultedColumn() throws Exception {
        Column originalColumn = new Column();
        originalColumn.setType("integer");
        originalColumn.setName("candles");
        originalColumn.setDefaultValue("4");

        Column expectedColumn = (Column) originalColumn.clone();
        expectedColumn.setDefaultValue(null);

        Column inputColumn = (Column) originalColumn.clone();

        String expectedAddSql = "ADD etc.";
        expect(getPlatform().getChangeDatabaseSql(AddSingleColumnMatcher.match(expectedColumn)))
            .andReturn(expectedAddSql);
        replayMocks();

        assertStatements(
            getDialect().addColumn("feast", inputColumn),
            expectedAddSql,
            "ALTER TABLE feast ALTER COLUMN candles SET DEFAULT '4'",
            "UPDATE feast SET candles='4' WHERE candles IS NULL"
        );
        assertNull("Column default not set null", inputColumn.getDefaultValue());
    }

    public void testAddNotNullColumn() throws Exception {
        Column originalColumn = new Column();
        originalColumn.setType("integer");
        originalColumn.setName("candles");
        originalColumn.setRequired(true);

        Column expectedColumn = (Column) originalColumn.clone();
        expectedColumn.setRequired(false);

        Column inputColumn = (Column) originalColumn.clone();

        String expectedAddSql = "ADD etc.";
        expect(getPlatform().getChangeDatabaseSql(AddSingleColumnMatcher.match(expectedColumn)))
            .andReturn(expectedAddSql);
        replayMocks();

        assertStatements(
            getDialect().addColumn("feast", inputColumn),
            expectedAddSql,
            "ALTER TABLE feast ALTER COLUMN candles SET NOT NULL"
        );
        assertFalse("Column requiredness not cleared", inputColumn.isRequired());
    }

    private static class AddSingleColumnMatcher implements IArgumentMatcher {
        private Column expected;

        public AddSingleColumnMatcher(Column expected) {
            this.expected = expected;
        }

        public boolean matches(Object actual) {
            List<AddColumnChange> actualChanges = (List<AddColumnChange>) actual;
            Column actualColumn = actualChanges.get(0).getNewColumn();
            return actualChanges.size() == 1 && expected.equals(actualColumn);
        }

        public void appendTo(StringBuffer sb) {
            sb.append("AddSingleColumnMatcher.match(")
                .append(expected)
                .append(')');
        }

        public static List<AddColumnChange> match(Column expected) {
            reportMatcher(new AddSingleColumnMatcher(expected));
            return null;
        }
    }
}
