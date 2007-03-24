package edu.northwestern.bioinformatics.bering.runtime;

import edu.northwestern.bioinformatics.bering.BeringTestCase;

/**
 * @author rsutphin
 */
public class ReleaseTest extends BeringTestCase {
    public void testNameAndIndexWithName() throws Exception {
        Release r = new Release("001_out_the_door");
        assertEquals(1, (int) r.getNumber());
        assertEquals("out_the_door", r.getName());
    }

    public void testNameAndIndexWithoutName() throws Exception {
        Release r = new Release("001");
        assertEquals(1, (int) r.getNumber());
        assertNull(r.getName());
    }

    public void testNaturalOrder() throws Exception {
        Release r1 = new Release("001_one");
        Release r2 = new Release("002");
        Release r3 = new Release("003_three");
        assertNegative(r1.compareTo(r2));
        assertNegative(r1.compareTo(r3));
        assertPositive(r2.compareTo(r1));
        assertNegative(r2.compareTo(r3));
        assertPositive(r3.compareTo(r1));
        assertPositive(r3.compareTo(r2));
    }
}
