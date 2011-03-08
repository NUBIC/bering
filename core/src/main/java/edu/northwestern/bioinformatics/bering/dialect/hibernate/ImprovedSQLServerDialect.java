package edu.northwestern.bioinformatics.bering.dialect.hibernate;

import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.SQLServerDialect;

import java.sql.Types;

/**
 * SQL Server dialect for hibernate that works with the Bering ethos.  It is mostly the same as
 * the hibernate-provided one, with these exceptions:
 * <ul>
 * <li>Uses "VARCHAR(8000)" type for unlimited <code>Types.VARCHAR</code> columns</li>
 * <li>Uses "VARCHAR(n)" type for limited <code>Types.VARCHAR</code> columns</li>
 * <li>Uses "BIT" type for boolean columns</li>
 * </ul>
 *
 * @author Eric Wyles (ewyles@uams.edu)
 */
public class ImprovedSQLServerDialect extends SQLServerDialect {
    public ImprovedSQLServerDialect() {
        super();
        registerColumnType(Types.VARCHAR, "varchar(8000)");
        registerColumnType(Types.VARCHAR, 8000, "varchar($l)");
        registerColumnType(Types.BIT, "bit");
    }
}
