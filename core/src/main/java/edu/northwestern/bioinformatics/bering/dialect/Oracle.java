package edu.northwestern.bioinformatics.bering.dialect;

import edu.northwestern.bioinformatics.bering.Column;
import static edu.northwestern.bioinformatics.bering.SqlUtils.sqlLiteral;
import edu.northwestern.bioinformatics.bering.TableDefinition;
import edu.northwestern.bioinformatics.bering.TypeQualifiers;
import edu.northwestern.bioinformatics.bering.dialect.hibernate.ImprovedOracleDialect;
import org.apache.commons.lang.StringUtils;
import org.hibernate.dialect.Dialect;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Rhett Sutphin
 */
public class Oracle extends HibernateBasedDialect {
    private static final int MAX_IDENTIFIER_LENGTH = 30;
    private static final int VARCHAR2_CHAR_LIMIT = 2000;

    @Override
    protected Dialect createHibernateDialect() {
        return new ImprovedOracleDialect();
    }

    @Override
    public String getDialectName() {
        return "oracle";
    }

    @Override
    public List<String> createTable(TableDefinition def) {
        List<String> statements = new ArrayList<String>(2);
        statements.addAll(super.createTable(def));
        if (def.getIncludePrimaryKey()) {
            statements.add(String.format("CREATE SEQUENCE %s", createIdSequenceName(def.getName())));
        }
        return statements;
    }

    @Override
    public List<String> renameTable(String table, String newName, boolean hasPrimaryKey) {
        List<String> statements = new ArrayList<String>(2);
        statements.addAll(super.renameTable(table, newName, hasPrimaryKey));
        if (hasPrimaryKey) {
            statements.add(String.format(
                "RENAME %s TO %s", createIdSequenceName(table), createIdSequenceName(newName)));
        }
        return statements;
    }

    @Override
    public List<String> dropTable(String tableName, boolean primaryKey) {
        List<String> statments = new ArrayList<String>(2);
        statments.addAll(super.dropTable(tableName, primaryKey));
        if (primaryKey) statments.add("DROP SEQUENCE " + createIdSequenceName(tableName));
        return statments;
    }

    @Override
    public List<String> setDefaultValue(String table, String column, String newDefault) {
        return Arrays.asList(String.format(
            "ALTER TABLE %s MODIFY (%s DEFAULT %s)", table, column, sqlLiteral(newDefault)
        ));
    }

    @Override
    public List<String> setNullable(String table, String column, boolean nullable) {
        return Arrays.asList(String.format(
            "ALTER TABLE %s MODIFY (%s %sNULL)", table, column, nullable ? "" : "NOT "
        ));
    }

    @Override
    public List<String> addColumn(String table, Column column) {
        List<String> statements = new ArrayList<String>(2);
        statements.add(String.format(
            "ALTER TABLE %s ADD (%s)",
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
    protected TypeQualifiers getDefaultTypeQualifiers(int typeCode) {
        switch (typeCode) {
            case Types.VARCHAR: return new TypeQualifiers(VARCHAR2_CHAR_LIMIT, null, null);
            default: return super.getDefaultTypeQualifiers(typeCode);
        }
    }

    private String createIdSequenceName(String tableName) {
        return createIdentifier("seq_", tableName, "_id");
    }

    private String createPkConstraintName(String tableName) {
        return createIdentifier("pk_", tableName, null);
    }

    protected String createForeignKeyConstraintName(String tableName, Column column) {
        if (column.getTableReferenceName() != null) {
            return column.getTableReferenceName();
        } else {
            int subnamelen = (MAX_IDENTIFIER_LENGTH - 4) / 2;
            return String.format("fk_%s_%s",
                truncate(tableName, subnamelen),
                truncate(column.getTableReference(), subnamelen));
        }
    }

    private String createIdentifier(String prefix, String basename, String suffix) {
        int truncateBy = (prefix == null ? 0 : prefix.length()) + (suffix == null ? 0 : suffix.length());

        StringBuilder ident = new StringBuilder(MAX_IDENTIFIER_LENGTH);
        if (prefix != null) ident.append(prefix);
        ident.append(truncate(basename, MAX_IDENTIFIER_LENGTH - truncateBy));
        if (suffix != null) ident.append(suffix);

        return ident.toString();
    }

    private String truncate(String str, int maxlen) {
        if (str.length() <= maxlen) return str;
        return str.substring(0, maxlen);
    }

    // Attempts to be SQLPLUS-compatible for scripts that mix PL/SQL and plain SQL
    @Override
    public List<String> separateStatements(String script) {
        List<String> statments = new LinkedList<String>();

        // Filter out \r\n
        script = script.replaceAll("\r\n", "\n");
        // Filter out lone \r
        script = script.replaceAll("\r", "\n");

        // Split by '/' on a line by itself
        String[] blocks = script.split("[\\r\\n]+/[\\r\\n]+");

        // For each resulting block (except the last),
        for (int i = 0 ; i < blocks.length - 1 ; i++) {
            String block = blocks[i];
            // Walk back to the previous CREATE -- it is the beginning of the last statement in the block.
            int lastCreate = block.toUpperCase().lastIndexOf("CREATE");
            if (lastCreate < 0) {
                throw new IllegalArgumentException("Block ends with '/' but does not include a CREATE");
            }
            String last = block.substring(lastCreate);
            String balance = block.substring(0, lastCreate);

            // Split the remainder on semi-colons
            splitBySemiColons(balance, statments);

            statments.add(last.trim());
        }

        // The last block doesn't end with a '/', so include all its statements
        splitBySemiColons(blocks[blocks.length - 1], statments);

        return statments;
    }

    private void splitBySemiColons(String block, List<String> addTo) {
        String[] bStatements = block.split("\\s*;\\s*");
        for (String bStatement : bStatements) {
            if (bStatement.length() > 0) addTo.add(bStatement);
        }
    }

    @Override
    public List<String> insert(String table, List<String> columns, List<Object> values, boolean automaticPrimaryKey) {
        if (columns.size() == 0) {
            return Arrays.asList(String.format(
                "INSERT INTO %s (id) VALUES (%s.nextval)", table, createIdSequenceName(table)
            ));
        } else if (automaticPrimaryKey) {
            return Arrays.asList(String.format(
                "INSERT INTO %s (id, %s) VALUES (%s.nextval, %s)",
                    table,
                    StringUtils.join(columns.iterator(), INSERT_DELIMITER),
                    createIdSequenceName(table),
                    createInsertValueString(values)
            ));
        } else {
            return super.insert(table, columns, values, automaticPrimaryKey);
        }
    }
}
