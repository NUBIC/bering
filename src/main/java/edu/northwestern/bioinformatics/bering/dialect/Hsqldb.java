package edu.northwestern.bioinformatics.bering.dialect;

import edu.northwestern.bioinformatics.bering.TypeQualifiers;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.HSQLDialect;

import java.sql.Types;
import java.util.List;

/**
 * @author Rhett Sutphin
 */
public class Hsqldb extends HibernateBasedDialect {
    @Override
    protected Dialect createHibernateDialect() {
        return new HSQLDialect();
    }

    @Override
    public String getDialectName() {
        return "hsqldb";
    }

    @Override
    protected TypeQualifiers getDefaultTypeQualifiers(int typeCode) {
        switch (typeCode) {
            case Types.VARCHAR: return new TypeQualifiers(Integer.MAX_VALUE, null, null);
            default: return null;
        }
    }

    @Override
    public List<String> renameColumn(String tableName, String columnName, String newColumnName) {
        return singleStatement(
            "ALTER TABLE %s ALTER COLUMN %s RENAME TO %s", tableName, columnName, newColumnName
        );
    }
}
