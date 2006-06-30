package edu.northwestern.bioinformatics.bering.runtime;

import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author rsutphin
 */
public class Version {
    private SortedMap<Integer, Integer> versionTable;

    public Version() {
        versionTable = new TreeMap<Integer, Integer>();
    }

    public void updateRelease(Integer releaseNumber, Integer migrationNumber) {
        if (migrationNumber < 1) {
            versionTable.remove(releaseNumber);
        } else {
            versionTable.put(releaseNumber, migrationNumber);
        }
    }

    public Integer getMigrationNumber(Integer releaseNumber) {
        Integer migrationNumber = versionTable.get(releaseNumber);
        if (migrationNumber == null) {
            return 0;
        } else {
            return migrationNumber;
        }
    }

    public boolean containsRelease(Integer releaseNumber) {
        return versionTable.containsKey(releaseNumber);
    }

    public SortedSet<Integer> getReleaseNumbers() {
        return new TreeSet<Integer>(versionTable.keySet());
    }

    public Integer getLastReleaseNumber() {
        if (versionTable.size() > 0) {
            return versionTable.lastKey();
        } else {
            return 0;
        }
    }
}
