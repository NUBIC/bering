package edu.northwestern.bioinformatics.bering.runtime;

import edu.northwestern.bioinformatics.bering.BeringTestCase;

import java.util.List;

/**
 * @author rsutphin
 */
public class MigrationDifferenceTest extends BeringTestCase {
    public void testAllUpFromZero() throws Exception {
        assertMigrationsToRun(
            createDifference(0, 5,    1, 2, 3, 4, 5),
            true, 1, 2, 3, 4, 5);
    }

    public void testAllDownToZero() throws Exception {
        assertMigrationsToRun(
            createDifference(5, 0,    1, 2, 3, 4, 5),
            false, 5, 4, 3, 2, 1);
    }

    public void testPartialUp() throws Exception {
        assertMigrationsToRun(
            createDifference(2, 4,    1, 2, 3, 4, 5),
            true, 3, 4);
    }

    public void testPartialDown() throws Exception {
        assertMigrationsToRun(
            createDifference(4, 2,    1, 2, 3, 4, 5),
            false, 4, 3);
    }

    public void testNoop() throws Exception {
        assertMigrationsToRun(createDifference(3, 3,    1, 2, 3, 4, 5));
        assertMigrationsToRun(createDifference(0, 0,    1));
    }

    public void testUpWithGaps() throws Exception {
        assertMigrationsToRun(
            createDifference(0, 5,    1, 2, 5),
            true, 1, 2, 5);
    }

    public void testDownWithGaps() throws Exception {
        assertMigrationsToRun(
            createDifference(5, 0,    1, 2, 5),
            false, 5, 2, 1);
    }

    public void testPartialUpWithGaps() throws Exception {
        assertMigrationsToRun(
            createDifference(2, 5,    1, 2, 4, 5),
            true, 4, 5);
    }

    public void testPartialDownWithGaps() throws Exception {
        assertMigrationsToRun(
            createDifference(4, 2,    1, 2, 4, 5),
            false, 4);
    }

    private void assertMigrationsToRun(MigrationDifference diff) {
        // the fact that isUp returns true when there are no migrations
        // is an unimportant implementation detail
        assertMigrationsToRun(diff, true);
    }

    private void assertMigrationsToRun(MigrationDifference diff, boolean expectedIsUp, int... expectedNumbers) {
        assertEquals("Should be " + (expectedIsUp ? "up" : "down"), expectedIsUp, diff.isUp());
        assertScriptOrder(diff.getScriptsToRun(), expectedNumbers);
    }

    private void assertScriptOrder(List<Script> actual, int... expectedNumbers) {
        for (int i = 0; i < expectedNumbers.length; i++) {
            assertEquals("Mismatch at index " + i + " of " + actual,
                expectedNumbers[i], (int) actual.get(i).getNumber());
        }
    }

    private MigrationDifference createDifference(int current, int target, int... scriptNumbers) {
        Release release = new Mock.Release(1, scriptNumbers);
        return new MigrationDifference(release, current, target);
    }
}
