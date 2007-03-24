package edu.northwestern.bioinformatics.bering.runtime;

import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author Rhett Sutphin
 */
public class AbstractMigrationFinder implements MigrationFinder {
    protected SortedMap<Integer, Release> releases;

    public AbstractMigrationFinder() {
        this.releases = new TreeMap<Integer, Release>();
    }

    protected void addRelease(Release release) {
        releases.put(release.getNumber(), release);
    }

    public Collection<Release> getReleases() {
        return releases.values();
    }

    public int getMaxReleaseNumber() {
        return releases.lastKey();
    }

    public Release getRelease(int releaseNumber) {
        return releases.get(releaseNumber);
    }
}
