package edu.northwestern.bioinformatics.bering.runtime;

import java.io.File;

import edu.northwestern.bioinformatics.bering.BeringTestCase;

/**
 * @author rsutphin
 */
public class ReleaseTest extends BeringTestCase {
    private Release existingRelease;

    protected void setUp() throws Exception {
        super.setUp();
        existingRelease = new Release(
            getClassRelativeFile(getClass(), "../test_db/001_out_the_door"));
    }

    public void testNameAndIndexWithName() throws Exception {
        Release r = createRelease("../test_db/001_out_the_door");
        assertEquals(1, (int) r.getIndex());
        assertEquals("out_the_door", r.getName());
    }

    public void testNameAndIndexWithoutName() throws Exception {
        Release r = createRelease("../test_db/001");
        assertEquals(1, (int) r.getIndex());
        assertNull(r.getName());
    }

    public void testNaturalOrder() throws Exception {
        Release r1 = createRelease("001_one");
        Release r2 = createRelease("002");
        Release r3 = createRelease("003_three");
        assertNegative(r1.compareTo(r2));
        assertNegative(r1.compareTo(r3));
        assertPositive(r2.compareTo(r1));
        assertNegative(r2.compareTo(r3));
        assertPositive(r3.compareTo(r1));
        assertPositive(r3.compareTo(r2));
    }

    public void testScriptsFound() throws Exception {
        Release out = existingRelease.initialize();
        assertEquals(2, out.getScripts().size());
        assertEquals(1, (int) out.getScripts().get(0).getIndex());
        assertEquals("add_frogs", out.getScripts().get(0).getName());
        assertEquals(2, (int) out.getScripts().get(1).getIndex());
        assertEquals("add_ponds", out.getScripts().get(1).getName());
    }

    private Release createRelease(String dirname) {
        return new Release(new File(dirname));
    }
}
