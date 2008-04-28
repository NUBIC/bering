package edu.northwestern.bioinformatics.bering.dialect;

import edu.northwestern.bioinformatics.bering.TableDefinition;
import edu.northwestern.bioinformatics.bering.Column;

import java.util.List;
import java.sql.Types;

/**
 * @author Eric Wyles (ewyles@uams.edu)
 */
public class SqlServerTest extends HibernateBasedDialectTestCase<SqlServer> {

    private String strSetNullabilityPrefix = "declare @column_data_type varchar(256) Select @column_data_type = [data_type] from INFORMATION_SCHEMA.COLUMNS where table_name='feast' and column_name='length' declare @column_length varchar(256) declare @maxlen varchar(256) Select @maxlen = [character_maximum_length] from INFORMATION_SCHEMA.COLUMNS where character_maximum_length>=1 and table_name='feast' and column_name='length' if (@column_data_type = 'VARCHAR') Set @column_length = '(' + @maxlen + ')' else Set @column_length='' exec('ALTER TABLE feast ALTER COLUMN length ' + @column_data_type + @column_length + ";
    
    @Override
    protected Class<SqlServer> getDialectClass() {
        return SqlServer.class;
    }

    public void testName() throws Exception {
        assertEquals("sqlserver", getDialect().getDialectName());
    }

    public void testCreateTable() throws Exception {
        TableDefinition def = createTestTable();
        String expectedSql = "CREATE TABLE feast (\n  id INT IDENTITY NOT NULL,\n  name VARCHAR(8000),\n  length INT,\n  PRIMARY KEY(id)\n)";

        List<String> statements = getDialect().createTable(def);
        assertStatements(statements, expectedSql);
    }

    public void testSetNullable() throws Exception {
        assertStatements(
            getDialect().setNullable("feast", "length", true),
            strSetNullabilityPrefix + "' NULL')"
        );
    }

    public void testSetNotNullable() throws Exception {
        assertStatements(
            getDialect().setNullable("feast", "length", false),
            strSetNullabilityPrefix + "' NOT NULL')"
        );
    }

    public void testRenameTableWithPk() throws Exception {
        testRenameTableWithoutPk();
    }

    public void testRenameTableWithoutPk() throws Exception {
        String tableName = "test";
        String newTableName = "t_test";

        assertStatements(
            getDialect().renameTable(tableName, newTableName, false),
            "EXEC sp_rename 'test', 't_test'"
        );
    }

    public void testAddConstrainedString() throws Exception {
        Column col = new Column();
        col.getTypeQualifiers().setLimit(14);
        col.setName("n");
        col.setTypeCode(Types.VARCHAR);
        assertStatements(
            getDialect().addColumn("feast", col),
            "ALTER TABLE feast ADD n VARCHAR(14)"
        );
    }

    @Override
    protected String expectedAddStringStatement() {
        return "ALTER TABLE t ADD c VARCHAR(8000)";
    }

    @Override
    protected String expectedAddIntegerStatement() {
        return "ALTER TABLE t ADD c INT";
    }

    @Override
    protected String expectedAddFloatStatement() {
        return "ALTER TABLE t ADD c FLOAT";
    }

    @Override
    protected String expectedAddNumericStatement() {
        return "ALTER TABLE t ADD c NUMERIC";
    }

    @Override
    protected String expectedAddBooleanStatement() {
        return "ALTER TABLE t ADD c BIT";
    }

    @Override
    protected String expectedAddDateStatement() {
        return "ALTER TABLE t ADD c DATETIME";
    }

    @Override
    protected String expectedAddTimeStatement() {
        return "ALTER TABLE t ADD c DATETIME";
    }

    @Override
    protected String expectedAddTimestampStatement() {
        return "ALTER TABLE t ADD c DATETIME";
    }
}
