package edu.northwestern.bioinformatics.bering.runtime;

import edu.northwestern.bioinformatics.bering.Adapter;

import java.util.List;
import java.util.Collections;
import java.util.ArrayList;

/**
 * @author Rhett Sutphin
 * @author Moses Hohman
 */
public class Migrator {
    private Adapter adapter;
    private Version current;
    private Target target;
    private MigrationFinder finder;

    public Migrator(Adapter adapter, MigrationFinder finder, Version current, Target target) {
        this.finder = finder;
        this.current = current;
        this.target = target;
        this.adapter = adapter;
    }

    public void migrate() {
        if (current.getLastReleaseNumber() <= target.getReleaseNumber()) {
            upReleases();
        } else {
            downReleases();
        }
        migrateRelease(target.getReleaseNumber(), target.getMigrationNumber());
    }

    private void upReleases() {
        // go through releases from low to high, running all outstanding scripts
        // until you get to the target release
        for (Release release : finder.getReleases()) {
            Integer releaseN = release.getNumber();
            if (releaseN < target.getReleaseNumber()) {
                migrateRelease(releaseN, null);
            }
        }
    }

    private void downReleases() {
        // walk down to the end of the target release
        List<Release> reversedReleases = new ArrayList<Release>(finder.getReleases());
        Collections.reverse(reversedReleases);
        for (Release release : reversedReleases) {
            int releaseN = release.getNumber();
            if (releaseN > target.getReleaseNumber()) {
                migrateRelease(releaseN, 0);
            }
        }
    }

    private void migrateRelease(Integer releaseNumber, Integer targetMigration) {
        MigrationDifference diff = new MigrationDifference(
            finder.getRelease(releaseNumber), current.getMigrationNumber(releaseNumber),
            targetMigration
        );
        for (Script script : diff.getScriptsToRun()) {
            createExecutor(diff.isUp(), script).execute();
        }
    }

    private Executor createExecutor(boolean isUp, Script script) {
        if (isUp) {
            return new Up(script);
        } else {
            return new Down(script);
        }
    }

    private abstract class Executor {
        private Script script;
        private int resultingMigrationNumber;

        protected Executor(Script script, int resultingMigrationNumber) {
            this.script = script;
            this.resultingMigrationNumber = resultingMigrationNumber;
        }

        public void execute() {
            System.out.println("Executing migration: " + script.getClassName() + '.' + direction());
            adapter.beginTransaction();
            try {
                run();
                updateVersion(script.getRelease().getNumber(), resultingMigrationNumber);
                adapter.commit();
            } catch (RuntimeException e) {
                adapter.rollback();
                throw e;
            }
        }

        private void updateVersion(int release, int migration) {
            adapter.updateVersion(release, migration);
            current.updateRelease(release, migration);
        }

        public Script getScript() {
            return script;
        }

        protected abstract void run();

        protected abstract String direction();
    }

    private class Up extends Executor {
        public Up(Script script) {
            super(script, script.getNumber());
        }

        protected void run() {
            getScript().up(adapter);
        }

        protected String direction() {
            return "up";
        }
    }

    private class Down extends Executor {
        public Down(Script script) {
            super(script, script.getNumber() - 1);
        }

        protected void run() {
            getScript().down(adapter);
        }

        protected String direction() {
            return "down";
        }
    }
}
