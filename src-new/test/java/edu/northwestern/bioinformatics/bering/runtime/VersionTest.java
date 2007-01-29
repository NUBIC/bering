package edu.northwestern.bioinformatics.bering.runtime;

import edu.northwestern.bioinformatics.bering.BeringTestCase;

import java.util.Collections;
import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author rsutphin
 */
public class VersionTest extends BeringTestCase {
    private Version version = new Version();

    protected void setUp() throws Exception {
        super.setUp();
        version.updateRelease(1, 4);
        version.updateRelease(3, 7);
    }

    public void testNewRelease() throws Exception {
        version.updateRelease(4, 5);
        assertEquals(5, (int) version.getMigrationNumber(4));
        assertReleaseNumbers(1, 3, 4);
    }

    public void testUpdateRelease() throws Exception {
        version.updateRelease(3, 5);
        assertEquals(5, (int) version.getMigrationNumber(3));
        assertReleaseNumbers(1, 3);
    }

    public void testUpdateToZero() throws Exception {
        version.updateRelease(3, 0);
        assertReleaseNumbers(1);
    }

    public void testGetLastReleaseNumber() throws Exception {
        assertEquals(3, (int) version.getLastReleaseNumber());
    }

    public void testGetDefaultLastReleaseNumber() throws Exception {
        assertEquals(0, (int) new Version().getLastReleaseNumber());
    }

    private void assertReleaseNumbers(Integer... expected) {
        assertEquals(
            new TreeSet<Integer>(Arrays.asList(expected)),
            version.getReleaseNumbers()
        );
    }
}
