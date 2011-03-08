package edu.northwestern.bioinformatics.bering.dialect;

import edu.northwestern.bioinformatics.bering.Column;
import edu.northwestern.bioinformatics.bering.SqlUtils;
import edu.northwestern.bioinformatics.bering.TableDefinition;
import edu.northwestern.bioinformatics.bering.TypeQualifiers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/**
 * @author Rhett Sutphin
 */
public abstract class HibernateBasedDialect extends AbstractDialect {
    /* These values are from org.hibernate.mapping.Column */
    private static final TypeQualifiers GLOBAL_DEFAULT_TYPE_QUALIFIERS = new TypeQualifiers(255, 19, 2);
    private static final Pattern TYPE_QUALIFIER_IN_NAME_PATTERN = Pattern.compile("\\(.*?\\$.*?\\)");

    private org.hibernate.dialect.Dialect hibernateDialect;

    protected HibernateBasedDialect() {
        hibernateDialect = createHibernateDialect();
    }

    protected abstract org.hibernate.dialect.Dialect createHibernateDialect();

    public String getDialectName() {
        String name = getHibernateDialect().getClass().getSimpleName().toLowerCase();
        return name.substring(0, name.length() - 7);
    }

    private org.hibernate.dialect.Dialect getHibernateDialect() {
        return hibernateDialect;
    }

    public List<String> createTable(TableDefinition table) {
        List<String> colDeclarations = new ArrayList<String>(table.getColumnCount());
        List<String> tableConstraints = new ArrayList<String>();
        for (Column col : table.getColumns()) {
            if (col == Column.AUTOMATIC_PK) {
                colDeclarations.add(String.format("id %s", getAutoPkColumnString()));
                tableConstraints.add("PRIMARY KEY(id)");
            } else {
                colDeclarations.add(new ColumnDeclaration(col).toSql());
                if (col.getTableReference() != null) {
                    tableConstraints.add(createForeignKeyConstraintClause(table.getName(), col));
                }
            }
        }
        StringBuilder statement = new StringBuilder(
            String.format("CREATE TABLE %s (\n", table.getName()));

        for (java.util.Iterator<String> it = colDeclarations.iterator(); it.hasNext();) {
            statement.append("  ").append(it.next());
            if (it.hasNext()) statement.append(",\n");
        }
        if (!tableConstraints.isEmpty()) statement.append(',');
        statement.append('\n');

        for (Iterator<String> it = tableConstraints.iterator(); it.hasNext();) {
            statement.append("  ").append(it.next());
            if (it.hasNext()) statement.append(',');
            statement.append('\n');
        }

        statement.append(')');
        return Arrays.asList(statement.toString());
    }

    protected String createForeignKeyConstraintClause(String tableName, Column col) {
        return String.format("CONSTRAINT %s FOREIGN KEY (%s) REFERENCES %s(id)",
            createForeignKeyConstraintName(tableName, col),
            col.getName(), col.getTableReference());
    }

    protected String createForeignKeyConstraintName(String tableName, Column column) {
        if (column.getTableReferenceName() != null) {
            return column.getTableReferenceName();
        } else {
            return String.format("fk_%s_%s", tableName, column.getTableReference());
        }
    }

    /**
     * Template method allowing subclasses to specify a default limit, precision, and/or scale
     * for a given type.
     *
     * @param typeCode
     * @return an appropriate TypeQualifiers instance for the type, or null
     */
    protected TypeQualifiers getDefaultTypeQualifiers(int typeCode) {
        return null;
    }

    /**
     * Template method allowing subclasses to override the SQL generated for a DEFAULT [value]
     * declaration.
     * 
     * @param column
     * @return
     */
    protected String createDefaultValueSql(Column column) {
        return SqlUtils.sqlLiteral(column.getDefaultValue());
    }

    private String getAutoPkColumnString() {
        String typeName = new ColumnDeclaration(Column.AUTOMATIC_PK).getTypeName();
        StringBuilder sql = new StringBuilder();
        if (getHibernateDialect().supportsIdentityColumns()) {
            if (getHibernateDialect().hasDataTypeInIdentityColumn()) {
                sql.append(typeName).append(' ');
            }
            sql.append(getHibernateDialect().getIdentityColumnString(Column.AUTOMATIC_PK.getTypeCode()));
        } else {
            sql.append(typeName);
        }
        return sql.toString().toUpperCase();
    }

    public List<String> addColumn(String table, Column column) {
        List<String> statements = new ArrayList<String>(2);
        statements.add(String.format(
            "ALTER TABLE %s ADD COLUMN %s",
            table,
            new ColumnDeclaration(column).toSql()
        ));
        if (column.getTableReference() != null) {
            statements.add(String.format(
                "ALTER TABLE %s ADD %s",
                table, createForeignKeyConstraintClause(table, column)
            ));
        }
        return statements;
    }

    protected class ColumnDeclaration {
        private Column column;

        public ColumnDeclaration(Column column) {
            this.column = column;
        }

        public String toSql() {
            return String.format("%s %s%s%s%s",
                column.getName(), getTypeName(),
                createDefaultDeclaration(),
                createNotNullDeclaration(),
                createPrimaryKeyDeclaration()
            );
        }

        private String createNotNullDeclaration() {
            return column.isNullable() ? "" : " NOT NULL";
        }

        private String createPrimaryKeyDeclaration() {
            return column.isPrimaryKey() ? " PRIMARY KEY" : "";
        }

        private String createDefaultDeclaration() {
            if (column.getDefaultValue() != null) {
                return String.format(" DEFAULT %s", createDefaultValueSql(column));
            } else {
                return "";
            }
        }

        public String getTypeName() {
            int typeCode = column.getTypeCode();
            TypeQualifiers effective = effectiveTypeQualifiers();
            if (effective.isEmpty()) {
                String name = getHibernateDialect().getTypeName(typeCode);
                // strip out qualifier params
                name = StringUtils.join(TYPE_QUALIFIER_IN_NAME_PATTERN.split(name), "");
                return name.toUpperCase();
            } else {
                return getHibernateDialect().getTypeName(
                    typeCode, effective.getLimit(), effective.getPrecision(), effective.getScale()
                ).toUpperCase();
            }
        }

        private TypeQualifiers effectiveTypeQualifiers() {
            int typeCode = column.getTypeCode();
            TypeQualifiers merged = column.getTypeQualifiers();
            TypeQualifiers defaults = getDefaultTypeQualifiers(typeCode);
            if (defaults != null) {
                merged = merged.merge(defaults);
            }
            if (!merged.isEmpty()) {
                // add in other values from global defaults if any are set
                merged = merged.merge(GLOBAL_DEFAULT_TYPE_QUALIFIERS);
            }
            return merged;
        }
    }
}
