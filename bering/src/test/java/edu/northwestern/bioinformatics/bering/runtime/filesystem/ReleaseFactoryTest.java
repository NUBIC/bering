package edu.northwestern.bioinformatics.bering.runtime.filesystem;

import edu.northwestern.bioinformatics.bering.BeringTestCase;
import edu.northwestern.bioinformatics.bering.runtime.Release;
import edu.northwestern.bioinformatics.bering.runtime.Script;

/**
 * @author Rhett Sutphin
 */
public class ReleaseFactoryTest extends BeringTestCase {
    public void testScriptsFound() throws Exception {
        Release out = createExistingRelease("../../test_db/001_out_the_door");
        assertEquals(2, out.getScripts().size());
        assertEquals(1, (int) out.getScript(1).getNumber());
        assertEquals("add_frogs", out.getScript(1).getName());
        assertEquals(2, (int) out.getScript(2).getNumber());
        assertEquals("add_ponds", out.getScript(2).getName());
    }

    public void testDuplicateScriptNumberThrowsException() throws Exception {
        try {
            createExistingRelease("../../test_db_malformed/003_duplicate_numbers");
            fail("Exception not thrown");
        } catch (IllegalStateException ise) {
            assertEquals("More than one script in release 3 with number '2'", ise.getMessage());
        }
    }

    public void testLoadedScriptName() throws Exception {
        Script r1m1 = createExistingRelease("../../test_db/001_out_the_door").getScript(1);
        assertEquals("001_add_frogs", r1m1.getElementName());
    }

    private Release createExistingRelease(String dirname) {
        return new ReleaseFactory(getClassRelativeFile(getClass(), dirname)).create();
    }
}
