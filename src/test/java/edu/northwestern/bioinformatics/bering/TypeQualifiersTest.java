package edu.northwestern.bioinformatics.bering;

/**
 * @author Rhett Sutphin
 */
public class TypeQualifiersTest extends BeringTestCase {
    public void testEmptyByDefault() throws Exception {
        assertTrue(new TypeQualifiers().isEmpty());
    }

    public void testNotEmptyWithLimit() throws Exception {
        assertFalse(new TypeQualifiers(5, null, null).isEmpty());
    }

    public void testNotEmptyWithPrecision() throws Exception {
        assertFalse(new TypeQualifiers(null, 5, null).isEmpty());
    }

    public void testNotEmptyWithScale() throws Exception {
        assertFalse(new TypeQualifiers(null, null, 5).isEmpty());
    }
    
    public void testMergeClones() throws Exception {
        TypeQualifiers original = new TypeQualifiers(1, 2, 3);
        TypeQualifiers mergeWith = new TypeQualifiers(5, 6, 7);
        TypeQualifiers merged = original.merge(mergeWith);
        assertNotSame("Used original", original, merged);
        assertNotSame("Used operand", mergeWith, merged);
    }

    public void testMergePrefersSelf() throws Exception {
        TypeQualifiers original = new TypeQualifiers(1, 2, 3);
        TypeQualifiers merged = original.merge(new TypeQualifiers(8, 9, 7));
        assertTypeQualifiers(1, 2, 3, merged);
    }

    public void testMergeWorks() throws Exception {
        TypeQualifiers original = new TypeQualifiers();
        TypeQualifiers merged = original.merge(new TypeQualifiers(8, 9, 7));
        assertTypeQualifiers(8, 9, 7, merged);
    }

    public void testMergeWorksPartial() throws Exception {
        TypeQualifiers original = new TypeQualifiers(1, null, null);
        TypeQualifiers merged = original.merge(new TypeQualifiers(8, 9, 7));
        assertTypeQualifiers(1, 9, 7, merged);
    }

    private static void assertTypeQualifiers(
        Integer expectedLimit, Integer expectedPrecision, Integer expectedScale,
        TypeQualifiers actual
    ) {
        assertEquals("Wrong limit", expectedLimit, actual.getLimit());
        assertEquals("Wrong precision", expectedPrecision, actual.getPrecision());
        assertEquals("Wrong scale", expectedScale, actual.getScale());
    }

}
