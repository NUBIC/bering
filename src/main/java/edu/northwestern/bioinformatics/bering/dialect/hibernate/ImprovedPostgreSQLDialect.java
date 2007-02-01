package edu.northwestern.bioinformatics.bering.dialect.hibernate;

import org.hibernate.dialect.PostgreSQLDialect;
import org.hibernate.id.IdentityGenerator;

import java.sql.Types;

/**
 * PostgreSQL dialect for hibernate that works with the Bering ethos.  It is mostly the same as
 * the hibernate-provided one, with these exceptions:
 * <ul>
 * <li>Uses identity id generator in native mode (since Bering generates SERIAL PKs for PostgreSQL)</li>
 * <li>Uses "TEXT" type for unlimited <code>Types.VARCHAR</code> columns</li>
 * <li>Uses "BOOLEAN" type for <code>Types.BIT</code> columns
 *      (hibernate uses "BOOL", which appears to be for stored procs)</li>
 * </ul>
 *
 * @author Rhett Sutphin
 */
public class ImprovedPostgreSQLDialect extends PostgreSQLDialect {

    public ImprovedPostgreSQLDialect() {
        super();
        registerColumnType(Types.VARCHAR, "text");
        registerColumnType(Types.BIT, "boolean");
    }

    @Override
    public Class getNativeIdentifierGeneratorClass() {
        return IdentityGenerator.class;
    }
}
