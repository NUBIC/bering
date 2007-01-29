package edu.northwestern.bioinformatics.bering.runtime;

import edu.northwestern.bioinformatics.bering.BeringTestCase;

/**
 * @author rsutphin
 */
public class TargetTest extends BeringTestCase {
    private MigrationFinder finder;

    protected void setUp() throws Exception {
        super.setUp();
        finder = new MigrationFinder(getClassRelativeFile(getClass(), "../test_db"));
    }

    public void testConcreteCreate() throws Exception {
        assertTargetParameters(2, 1, Target.create(finder, 2, 1));
        assertTargetParameters(1, 1, Target.create(finder, 1, 1));
    }

    public void testCreateWithNullMigrationIsMax() throws Exception {
        assertTargetParameters(1, 2, Target.create(finder, 1, null));
    }

    public void testCreateWithNullReleaseIsMax() throws Exception {
        assertTargetParameters(2, 1, Target.create(finder, null, null));
    }

    private static void assertTargetParameters(int expectedRelease, int expectedMigration, Target actual) {
        assertEquals("Wrong target release",   expectedRelease, actual.getReleaseNumber());
        assertEquals("Wrong target migration", expectedMigration, actual.getMigrationNumber());
    }
}
