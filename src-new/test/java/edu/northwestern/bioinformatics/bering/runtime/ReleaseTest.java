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
        existingRelease = createExistingRelease("../test_db/001_out_the_door");
    }

    public void testNameAndIndexWithName() throws Exception {
        Release r = createRelease("../test_db/001_out_the_door");
        assertEquals(1, (int) r.getNumber());
        assertEquals("out_the_door", r.getName());
    }

    public void testNameAndIndexWithoutName() throws Exception {
        Release r = createRelease("../test_db/001");
        assertEquals(1, (int) r.getNumber());
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
        assertEquals(1, (int) out.getScript(1).getNumber());
        assertEquals("add_frogs", out.getScript(1).getName());
        assertEquals(2, (int) out.getScript(2).getNumber());
        assertEquals("add_ponds", out.getScript(2).getName());
    }

    public void testDuplicateScriptNumberThrowsException() throws Exception {
        try {
            createExistingRelease("../test_db_malformed/003_duplicate_numbers").initialize();
            fail("Exception not thrown");
        } catch (IllegalStateException ise) {
            assertEquals("More than one script in release 3 with number '2'", ise.getMessage());
        }
    }

    private Release createRelease(String dirname) {
        return new Release(new File(dirname));
    }

    private Release createExistingRelease(String dirname) {
        return new Release(getClassRelativeFile(getClass(), dirname));
    }
}
