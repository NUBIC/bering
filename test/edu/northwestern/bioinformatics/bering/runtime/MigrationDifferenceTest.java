package edu.northwestern.bioinformatics.bering.runtime;

import edu.northwestern.bioinformatics.bering.BeringTestCase;
import edu.northwestern.bioinformatics.bering.StubMigration;
import edu.northwestern.bioinformatics.bering.Migration;
import static org.easymock.classextension.EasyMock.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author rsutphin
 */
public class MigrationDifferenceTest extends BeringTestCase {
    private Release release;
    private List<Script> scripts;

    protected void setUp() throws Exception {
        super.setUp();
        release = createMock(Release.class);
    }

    public void testAllUpFromZero() throws Exception {
        assertMigrationsToRun(createDifference(5, 0, 5),
            true, 1, 2, 3, 4, 5);
    }

    public void testAllDownToZero() throws Exception {
        assertMigrationsToRun(createDifference(5, 5, 0),
            false, 5, 4, 3, 2, 1);
    }

    public void testPartialUp() throws Exception {
        assertMigrationsToRun(createDifference(5, 2, 4),
            true, 3, 4);
    }

    public void testPartialDown() throws Exception {
        assertMigrationsToRun(createDifference(5, 4, 2),
            false, 4, 3);
    }

    public void testNoop() throws Exception {
        assertMigrationsToRun(createDifference(5, 3, 3));
        resetMocks();
        assertMigrationsToRun(createDifference(1, 0, 0));
    }

    private void assertMigrationsToRun(MigrationDifference diff) {
        // the fact that isUp returns true when there are no migrations
        // is an unimportant implementation detail
        assertMigrationsToRun(diff, true);
    }

    private void assertMigrationsToRun(MigrationDifference diff, boolean expectedIsUp, int... expectedNumbers) {
        expectMigrations(expectedNumbers);
        replayMocks();

        assertEquals("Should be " + (expectedIsUp ? "up" : "down"), expectedIsUp, diff.isUp());
        assertMigrationsOrder(diff.getMigrationsToRun(), expectedNumbers);
        verifyMocks();
    }

    // this assertion is primarily to check the order
    private void assertMigrationsOrder(List<Migration> actual, int... expectedNumbers) {
        for (int i = 0; i < expectedNumbers.length; i++) {
            assertEquals("Mismatch at index " + i + " of " + actual,
                expectedNumbers[i], ((FakeMigration) actual.get(i)).getNumber());
        }
    }

    private void expectMigrations(int... migrationNumbers) {
        for (int n : migrationNumbers) {
            Script script = scripts.get(n - 1);
            expect(release.getScript(n)).andReturn(script);
            expect(script.createMigrationInstance()).andReturn(new FakeMigration(n));
        }
    }

    private MigrationDifference createDifference(int scriptCount, int current, int target) {
        declareScripts(scriptCount);
        return new MigrationDifference(release, current, target);
    }

    private void declareScripts(int scriptCount) {
        scripts = new ArrayList<Script>(scriptCount);
        while (scripts.size() < scriptCount) {
            scripts.add(createMock(Script.class));
        }
        expect(release.getScripts()).andReturn(scripts).anyTimes();
    }

    private void replayMocks() {
        replay(release);
        for (Script mock : scripts) {
            replay(mock);
        }
    }

    private void verifyMocks() {
        verify(release);
        for (Script mock : scripts) {
            verify(mock);
        }
    }

    private void resetMocks() {
        reset(release);
        for (Script mock : scripts) {
            reset(mock);
        }
        scripts.clear();
    }

    private static class FakeMigration extends StubMigration {
        private int number;

        public FakeMigration(int number) {
            this.number = number;
        }

        public int getNumber() {
            return number;
        }

        public String toString() {
            return new StringBuffer(getClass().getSimpleName())
                .append('[').append(getNumber()).append(']').toString();
        }
    }
}
