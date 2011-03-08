package edu.northwestern.bioinformatics.bering.runtime.classpath;

import edu.northwestern.bioinformatics.bering.BeringTestCase;
import edu.northwestern.bioinformatics.bering.runtime.Release;
import edu.northwestern.bioinformatics.bering.runtime.Script;
import org.apache.commons.io.FileUtils;

/**
 * This test covers {@link edu.northwestern.bioinformatics.bering.runtime.classpath.ReleaseFactory}, too.
 * 
 * @author Rhett Sutphin
 */
public class ClasspathMigrationFinderTest extends BeringTestCase {
    private ClasspathMigrationFinder finder;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        finder = new ClasspathMigrationFinder("/edu/northwestern/bioinformatics/bering/test_db");
    }

    public void testAllReleasesPresent() throws Exception {
        assertEquals(2, finder.getReleases().size());
        assertEquals("out_the_door", finder.getRelease(1).getName());
        assertEquals(1, (int) finder.getRelease(1).getNumber());
        assertEquals("lots_of_ponds", finder.getRelease(2).getName());
        assertEquals(2, (int) finder.getRelease(2).getNumber());
    }
    
    public void testScriptsFound() throws Exception {
        Release out = finder.getRelease(1);
        assertEquals(2, out.getScripts().size());
        assertEquals(1, (int) out.getScript(1).getNumber());
        assertEquals("add_frogs", out.getScript(1).getName());
        assertEquals(2, (int) out.getScript(2).getNumber());
        assertEquals("add_ponds", out.getScript(2).getName());
    }
    
    public void testScriptContentRead() throws Exception {
        String expectedContents = FileUtils.readFileToString(
            getClassRelativeFile(getClass(), "../../test_db/001_out_the_door/001_add_frogs.groovy"));
        Script r1m1 = finder.getRelease(1).getScript(1);
        assertEquals(expectedContents, r1m1.getScriptText());
    }
}
