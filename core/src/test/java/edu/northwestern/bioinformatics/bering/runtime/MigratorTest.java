package edu.northwestern.bioinformatics.bering.runtime;

import edu.northwestern.bioinformatics.bering.Adapter;
import edu.northwestern.bioinformatics.bering.BeringTestCase;
import edu.northwestern.bioinformatics.bering.MigrationExecutionException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author rsutphin
 */
public class MigratorTest extends BeringTestCase {
    private Mock.Finder finder;
    private Adapter adapter;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        finder = new Mock.Finder();
        finder.addRelease(1,    1, 2, 3, 4, 5);
        finder.addRelease(2,    1, 2);
        finder.addRelease(4,    1, 3, 4);
        finder.addRelease(7,    1, 2, 3);

        adapter = registerMockFor(Adapter.class);
    }

    private Migrator createMigrator(Version version, Integer targetRelease, Integer targetMigration) {
        return new Migrator(adapter, finder,
            version, Target.create(finder, targetRelease, targetMigration));
    }

    public void testUpAll() throws Exception {
        ExecutionExpectation execExpect = expectUp()
            .expectExecute(1,     1, 2, 3, 4, 5)
            .expectExecute(2,     1, 2)
            .expectExecute(4,     1, 3, 4)
            .expectExecute(7,     1, 2, 3);

        runTest(createMigrator(new Version(), null, null), execExpect);
    }

    public void testDownAll() throws Exception {
        Version v = new Version();
        v.updateRelease(1, 5);
        v.updateRelease(2, 2);
        v.updateRelease(4, 4);
        v.updateRelease(7, 3);

        ExecutionExpectation execExpect = expectDown()
            .expectExecute(7,     3, 2, 1)
            .expectExecute(4,     4, 3, 1)
            .expectExecute(2,     2, 1)
            .expectExecute(1,     5, 4, 3, 2, 1);

        runTest(createMigrator(v, 1, 0), execExpect);
    }

    public void testUpWithinRelease() throws Exception {
        ExecutionExpectation execExpect = expectUp()
            .expectExecute(1,    2, 3, 4, 5);

        Version v = new Version();
        v.updateRelease(1, 1);

        runTest(createMigrator(v, 1, 5), execExpect);
    }

    public void testDownWithinRelease() throws Exception {
        ExecutionExpectation execExpect = expectDown()
            .expectExecute(1,    5, 4, 3);

        Version v = new Version();
        v.updateRelease(1, 5);

        runTest(createMigrator(v, 1, 2), execExpect);
    }

    public void testUpWithPreviouslySkippedScripts() throws Exception {
        Version v = new Version();
        v.updateRelease(1, 5);
        v.updateRelease(2, 1);

        ExecutionExpectation execExpect = expectUp()
            .expectExecute(2, 2)
            .expectExecute(4, 1, 3);

        runTest(createMigrator(v, 4, 3), execExpect);
    }

    public void testDownWithPreviouslySkippedScripts() throws Exception {
        Version v = new Version();
        v.updateRelease(1, 5);
        v.updateRelease(2, 2);
        v.updateRelease(4, 3);
        v.updateRelease(7, 2);

        ExecutionExpectation execExpect = expectDown()
            .expectExecute(7, 2, 1)
            .expectExecute(4, 3, 1)
            .expectExecute(2, 2);

        runTest(createMigrator(v, 2, 1), execExpect);
    }

    public void testUpWithException() throws Exception {
        RuntimeException expected = new RuntimeException("Bad trouble");
        ExecutionExpectation execExpect = expectUp()
            .expectExecute(1,    2, 3, 4)
            .expectThrow(1, 5, expected);

        Version v = new Version();
        v.updateRelease(1, 1);

        runTest(createMigrator(v, 1, 5), execExpect, expected);
    }

    public void testDownWithException() throws Exception {
        RuntimeException expected = new RuntimeException("Bad trouble");
        ExecutionExpectation execExpect = expectDown()
            .expectExecute(1,    5, 4, 3)
            .expectThrow(1, 2, expected);

        Version v = new Version();
        v.updateRelease(1, 5);

        runTest(createMigrator(v, 1, 1), execExpect, expected);
    }

    private void runTest(Migrator migrator, ExecutionExpectation execExpectation) {
        try {
            execExpectation.expectTransactions();
            replayMocks();
            migrator.migrate();
        } finally {
            execExpectation.assertExecutions();
            verifyMocks();
        }
    }

    private void runTest(
        Migrator migrator, ExecutionExpectation execExpectation, RuntimeException expectedException
    ) {
        try {
            runTest(migrator, execExpectation);
            fail("Exception not thrown");
        } catch (MigrationExecutionException mee) {
            assertSame(expectedException, mee.getCause());
        }
    }

    private ExecutionExpectation expectUp() {
        return new ExecutionExpectation(true);
    }

    private ExecutionExpectation expectDown() {
        return new ExecutionExpectation(false);
    }

    private class ExecutionExpectation {
        // maps from release number to expected scripts, in order of execution
        private Map<Integer, List<Integer>> expectedExecutions
            = new HashMap<Integer, List<Integer>>();
        private RuntimeException expectedException;
        private int[] exceptionVersion = new int[2];

        private boolean up;

        public ExecutionExpectation(boolean up) {
            this.up = up;
        }

        public ExecutionExpectation expectExecute(Integer releaseNumber, Integer... migrations) {
            expectedExecutions.put(releaseNumber, Arrays.asList(migrations));
            return this;
        }

        public ExecutionExpectation expectThrow(Integer releaseNumber, Integer migration, RuntimeException exception) {
            expectedException = exception;
            exceptionVersion[0] = releaseNumber;
            exceptionVersion[1] = migration;
            return this;
        }

        public void expectTransactions() {
            for (Map.Entry<Integer, List<Integer>> entry : expectedExecutions.entrySet()) {
                for (Integer migrationNumber : entry.getValue()) {
                    adapter.beginTransaction();
                    int newMigrationNumber = up ? migrationNumber : migrationNumber - 1;
                    adapter.updateVersion(entry.getKey(), newMigrationNumber);
                    adapter.commit();
                }
            }
            if (expectedException != null) {
                Mock.Script script
                    = (Mock.Script) finder.getRelease(exceptionVersion[0]).getScript(exceptionVersion[1]);
                script.setRunException(expectedException);
                adapter.beginTransaction();
                adapter.rollback();
            }
        }

        public void assertExecutions() {
            for (Map.Entry<Integer, List<Integer>> entry : expectedExecutions.entrySet()) {
                Release release = finder.getRelease(entry.getKey());
                for (Script script : release.getScripts()) {
                    if (entry.getValue().contains(script.getNumber())) {
                        if (up) {
                            assertUpCalled(script);
                        } else {
                            assertDownCalled(script);
                        }
                    } else {
                        assertNotRun(script);
                    }
                }
            }
        }

        private void assertNotRun(Script script) {
            assertFalse("Up called on " + script, ((Mock.Script) script).upCalled());
            assertFalse("Down called on " + script, ((Mock.Script) script).downCalled());
        }

        private void assertUpCalled(Script script) {
            assertTrue("Up not called on " + script, ((Mock.Script) script).upCalled());
            assertFalse("Down called on " + script, ((Mock.Script) script).downCalled());
        }

        private void assertDownCalled(Script script) {
            assertFalse("Up called on " + script, ((Mock.Script) script).upCalled());
            assertTrue("Down not called on " + script, ((Mock.Script) script).downCalled());
        }

    }
}
