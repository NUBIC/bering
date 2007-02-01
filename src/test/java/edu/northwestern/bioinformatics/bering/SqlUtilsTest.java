package edu.northwestern.bioinformatics.bering;

/**
 * @author Rhett Sutphin
 */
public class SqlUtilsTest extends BeringTestCase {
    public void testStringLit() throws Exception {
        assertEquals("'abc'", SqlUtils.sqlLiteral("abc"));
    }
    
    public void testStringLitEscapesSingleQuotes() throws Exception {
        assertEquals("'a''c'", SqlUtils.sqlLiteral("a'c"));
    }
}
