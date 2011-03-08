package edu.northwestern.bioinformatics.bering.dialect.hibernate;

import org.hibernate.dialect.Oracle9Dialect;

import java.sql.Types;

/**
 * Oracle dialect for hibernate that works with the Bering ethos.  It is mostly the same as
 * the hibernate-provided one, with these exceptions:
 * <ul>
 * <li>Uses "VARCHAR2" type for unlimited columns of type string (<code>Types.VARCHAR</code>).
 *  Hibernate's default (LONG) is deprecated in Oracle.  (VARCHAR2 is used in combination with
 *  {@link edu.northwestern.bioinformatics.bering.dialect.HibernateBasedDialect#getDefaultTypeQualifiers}
 *  to create 2000 character columns.  If that's insufficient, <code>Types.CLOB</code> should be
 *  used.)
 * </li>
 * </ul>
 * Unlike {@link edu.northwestern.bioinformatics.bering.dialect.hibernate.ImprovedPostgreSQLDialect},
 * there's no reason to use it for general hibernate
 *
 * @author Rhett Sutphin
 */
public class ImprovedOracleDialect extends Oracle9Dialect {
    public ImprovedOracleDialect() {
        registerColumnType( Types.VARCHAR, "varchar2($l char)" );
    }
}
