package edu.northwestern.bioinformatics.bering.dialect;

/**
 * @author Rhett Sutphin
 */
public class OracleTest extends DdlUtilsDialectTestCase<Oracle> {
    protected Oracle createDialect() { return new Oracle(); }

    public void testSetDefault() throws Exception {
        assertStatements(getDialect().setDefaultValue("feast", "length", "8"),
            "ALTER TABLE feast MODIFY (length DEFAULT '8')"
        );
    }

    public void testUnsetDefault() throws Exception {
        assertStatements(getDialect().setDefaultValue("feast", "length", null),
            "ALTER TABLE feast MODIFY (length DEFAULT NULL)"
        );
    }

    public void testSetNullable() throws Exception {
        assertStatements(
            getDialect().setNullable("feast", "length", true),
            "ALTER TABLE feast MODIFY (length NULL)"
        );
    }
    
    public void testSetNotNullable() throws Exception {
        assertStatements(
            getDialect().setNullable("feast", "length", false),
            "ALTER TABLE feast MODIFY (length NOT NULL)"
        );
    }
}
