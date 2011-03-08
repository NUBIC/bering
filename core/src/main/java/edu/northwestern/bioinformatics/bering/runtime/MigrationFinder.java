package edu.northwestern.bioinformatics.bering.runtime;

import java.util.Collection;

/**
 * @author Rhett Sutphin
 */
public interface MigrationFinder {
    Collection<Release> getReleases();

    int getMaxReleaseNumber();

    Release getRelease(int releaseNumber);
}
