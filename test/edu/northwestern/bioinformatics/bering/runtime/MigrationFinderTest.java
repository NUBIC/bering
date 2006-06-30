package edu.northwestern.bioinformatics.bering.runtime;

import edu.northwestern.bioinformatics.bering.BeringTestCase;

/**
 * @author rsutphin
 */
public class MigrationFinderTest extends BeringTestCase {
    private static final String ROOT = "../test_db";

    private MigrationFinder finder;

    protected void setUp() throws Exception {
        super.setUp();
        finder = new MigrationFinder(getClassRelativeFile(getClass(), ROOT));
    }

    public void testAllReleasesPresent() throws Exception {
        assertEquals(2, finder.getReleases().size());
        assertEquals("out_the_door", finder.getRelease(1).getName());
        assertEquals(1, (int) finder.getRelease(1).getNumber());
        assertEquals("lots_of_ponds", finder.getRelease(2).getName());
        assertEquals(2, (int) finder.getRelease(2).getNumber());
    }
}
