package edu.northwestern.bioinformatics.bering;

import edu.northwestern.bioinformatics.bering.runtime.MigrationFinder;
import edu.northwestern.bioinformatics.bering.runtime.MigrationLoadingException;
import edu.northwestern.bioinformatics.bering.runtime.Release;
import edu.northwestern.bioinformatics.bering.runtime.Script;
import edu.northwestern.bioinformatics.bering.runtime.Version;

import java.io.File;

/**
 * @author rsutphin
 */
public class Main {
    private String rootDir;
    private Adapter adapter;

    /**
     * Migrate to the given release and migration, moving up or down as necessary.
     * If <code>targetMigrationNumber</code> is null, the database will be migrated to the
     * last script in <code>targetReleaseNumber</code>.  If <code>targetReleaseNumber</code>
     * is null, the target will be the latest release.
     *
     * @param targetReleaseNumber
     * @param targetMigrationNumber
     */
    public void migrate(Integer targetReleaseNumber, Integer targetMigrationNumber) {
        // load migration scripts
        MigrationFinder finder = new MigrationFinder(new File(rootDir));

        // validate release/migration combination
        Target target = Target.create(finder, targetReleaseNumber, targetMigrationNumber);

        // load version table
        Version current = adapter.loadVersions();

        // walk through scripts, running ones that haven't been run, up to / down to desired release/version


    }

    private static class Target {
        private int releaseNumber, migrationNumber;

        private Target(int releaseNumber, int migrationNumber) {
            this.releaseNumber = releaseNumber;
            this.migrationNumber = migrationNumber;
        }

        public static Target create(MigrationFinder finder, Integer requestedReleaseNumber, Integer requestedMigrationNumber) {
            int relN = requestedReleaseNumber == null
                ? finder.getMaxReleaseIndex()
                : requestedReleaseNumber;

            Release requestedRelease = finder.getRelease(relN);
            if (requestedRelease == null) {
                throw new MigrationLoadingException("There's no release number " + requestedMigrationNumber);
            }

            int migN = requestedMigrationNumber == null
                ? requestedRelease.getMaxScriptIndex()
                : requestedReleaseNumber;

            Script requestedMigrationScript = requestedRelease.getScript(migN);
            if (requestedMigrationScript != null) {
                throw new MigrationLoadingException("There's no migration number " + requestedMigrationNumber + " in release " + requestedReleaseNumber);
            }

            return new Target(relN, migN);
        }

        public int getReleaseNumber() {
            return releaseNumber;
        }

        public int getMigrationNumber() {
            return migrationNumber;
        }
    }
}
