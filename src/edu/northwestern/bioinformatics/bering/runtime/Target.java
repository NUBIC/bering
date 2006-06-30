package edu.northwestern.bioinformatics.bering.runtime;

import edu.northwestern.bioinformatics.bering.runtime.MigrationFinder;
import edu.northwestern.bioinformatics.bering.runtime.Release;
import edu.northwestern.bioinformatics.bering.runtime.MigrationLoadingException;
import edu.northwestern.bioinformatics.bering.runtime.Script;

/**
 * @author rsutphin
 */
public class Target {
    private int releaseNumber, migrationNumber;

    private Target(int releaseNumber, int migrationNumber) {
        this.releaseNumber = releaseNumber;
        this.migrationNumber = migrationNumber;
    }

    public static Target create(MigrationFinder finder, Integer requestedReleaseNumber, Integer requestedMigrationNumber) {
        int effectiveReleaseNumber = requestedReleaseNumber == null
            ? finder.getMaxReleaseNumber()
            : requestedReleaseNumber;

        Release requestedRelease = finder.getRelease(effectiveReleaseNumber);
        if (requestedRelease == null) {
            throw new MigrationLoadingException("There's no release number " + effectiveReleaseNumber);
        }

        int effectiveMigrationNumber = requestedMigrationNumber == null
            ? requestedRelease.getMaxScriptNumber()
            : requestedMigrationNumber;

        Script requestedMigrationScript = requestedRelease.getScript(effectiveMigrationNumber);
        if (effectiveMigrationNumber > 0 && requestedMigrationScript == null) {
            throw new MigrationLoadingException("There's no migration number " + effectiveMigrationNumber + " in release " + effectiveReleaseNumber);
        }

        return new Target(effectiveReleaseNumber, effectiveMigrationNumber);
    }

    public int getReleaseNumber() {
        return releaseNumber;
    }

    public int getMigrationNumber() {
        return migrationNumber;
    }
}
