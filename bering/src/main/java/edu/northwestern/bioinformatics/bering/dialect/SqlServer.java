package edu.northwestern.bioinformatics.bering.dialect;

import org.hibernate.dialect.Dialect;

import edu.northwestern.bioinformatics.bering.dialect.hibernate.ImprovedSQLServerDialect;
import edu.northwestern.bioinformatics.bering.Column;
import edu.northwestern.bioinformatics.bering.SqlUtils;

import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;

/**
 * @author Eric Wyles (ewyles@uams.edu)
 */
public class SqlServer extends HibernateBasedDialect {
    @Override
    protected Dialect createHibernateDialect() {
        return new ImprovedSQLServerDialect();
    }

    @Override
    public String getDialectName() {
        return "sqlserver";
    }

    @Override
    public List<String> insert(String table, List<String> columns, List<Object> values, boolean automaticPrimaryKey) {
        List<String> statements = new LinkedList<String>();
        
        if (!automaticPrimaryKey) {
            statements.add("if (OBJECTPROPERTY(object_id('" + table + "'), 'TableHasIdentity') = 1) SET IDENTITY_INSERT " + table + " ON");

             statements.add(String.format(
                "INSERT INTO %s (%s) VALUES (%s)",
                    table,
                    StringUtils.join(columns.iterator(), INSERT_DELIMITER),
                    createInsertValueString(values)
            ));            
       
            statements.add("if (OBJECTPROPERTY(object_id('" + table + "'), 'TableHasIdentity') = 1) SET IDENTITY_INSERT " + table + " OFF");
        } else {
            statements.addAll(super.insert(table, columns, values, automaticPrimaryKey));
        }
        
        return statements;
    }    
    
    @Override
    public List<String> addColumn(String table, Column column) {
        List<String> statements = new ArrayList<String>(2);
        statements.add(String.format(
            "ALTER TABLE %s ADD %s",
            table,
            new ColumnDeclaration(column).toSql()
        ));
        if (column.getTableReference() != null) {
            statements.add(String.format(
                "ALTER TABLE %s ADD %s",
                table,
                createForeignKeyConstraintClause(table, column)
            ));
        }
        return statements;
    } 
    
    @Override
    public List<String> dropColumn(String table, String column) {
        List<String> statements = new LinkedList<String>();
        
        statements.add(removeDefaultConstraint(table, column));
        statements.add(removeForeignKeyConstraint(table, column));
        statements.add(String.format("ALTER TABLE %s DROP COLUMN %s", table, column));
        
        return statements;
    }    
    
    @Override
    public List<String> renameColumn(String tableName, String columnName, String newColumnName) {
        return Arrays.asList(String.format(
            "EXEC sp_rename '%s.[%s]', '%s', 'COLUMN'", tableName, columnName, newColumnName
        ));
    }   
    
    @Override
    public List<String> setDefaultValue(String table, String column, String newDefault) {
        if (newDefault == null) {
            return Arrays.asList(removeDefaultConstraint(table, column));
        } else {
            return Arrays.asList(String.format(
                "ALTER TABLE %s ADD DEFAULT %s FOR %s", table, SqlUtils.sqlLiteral(newDefault), column
            ));
        }
    } 
    
    @Override
    public List<String> setNullable(String table, String column, boolean nullable) {
        String dataTypeSQL = "declare @column_data_type varchar(256) Select @column_data_type = [data_type] from INFORMATION_SCHEMA.COLUMNS where table_name='" + table + "' and column_name='" + column + '\'';
        String lengthSQL = "declare @column_length varchar(256) declare @maxlen varchar(256) Select @maxlen = [character_maximum_length] from INFORMATION_SCHEMA.COLUMNS where character_maximum_length>=1 and table_name='" + table + "' and column_name='" + column + "' if (@column_data_type = 'VARCHAR') Set @column_length = '(' + @maxlen + ')' else Set @column_length=''";
        
        return Arrays.asList(String.format(
            "%s %s exec('ALTER TABLE %s ALTER COLUMN %s ' + @column_data_type + @column_length + ' %sNULL')", dataTypeSQL, lengthSQL, table, column, nullable ? "" : "NOT "
        ));
    }
    
    @Override
    public List<String> renameTable(String table, String newName, boolean hasPrimaryKey) {
        return Arrays.asList(String.format(
            "EXEC sp_rename '%s', '%s'", table, newName
        ));
    }    
    
    protected String removeDefaultConstraint(String table, String column)
    {
        String columnDefaultVariableName = "default_name_" + table + '_' + column;
        
        String setVariableSelect = "@" + columnDefaultVariableName + " = [name]";
        String checkExistenceSelect = "[name]";
        
        String fromAndWhere = "from sys.default_constraints where parent_object_id = object_id('" + table + "') and col_name(parent_object_id, parent_column_id) = '" + column + "'";
        
        String setVariableSQL = "select " + setVariableSelect + ' ' + fromAndWhere;
        String checkExistenceSQL = "select " + checkExistenceSelect + ' ' + fromAndWhere;
        return String.format("declare @" + columnDefaultVariableName + " varchar(256) " + setVariableSQL + " if exists (" + checkExistenceSQL + ") exec('alter table " + table + " drop constraint ' + @" + columnDefaultVariableName + ")");
    }
    
    protected String removeForeignKeyConstraint(String table, String column)
    {
        String columnfkVariableName = "fk_name_" + table + '_' + column;
        
        String setVariableSelect = "@" + columnfkVariableName + " = f.name";
        String checkExistenceSelect = "f.name";
        
        String fromAndWhere = "FROM sys.foreign_keys AS f INNER JOIN sys.foreign_key_columns AS fc ON f.OBJECT_ID = fc.constraint_object_id where OBJECT_NAME(f.parent_object_id)='" + table + "' and COL_NAME(fc.parent_object_id, fc.parent_column_id)='" + column + "'";
        
        String setVariableSQL = "select " + setVariableSelect + ' ' + fromAndWhere;
        String checkExistenceSQL = "select " + checkExistenceSelect + ' ' + fromAndWhere;
        return String.format("declare @" + columnfkVariableName + " varchar(256) " + setVariableSQL + " if exists (" + checkExistenceSQL + ") exec('alter table " + table + " drop constraint ' + @" + columnfkVariableName + ")");
    }
}
