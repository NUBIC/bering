package edu.northwestern.bioinformatics.bering.runtime;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Collection;
import java.util.regex.Pattern;

/**
 * @author rsutphin
 */
public class MigrationFinder {
    private File root;
    private SortedMap<Integer, Release> releases;

    public MigrationFinder(File root) {
        this.root = root;
        this.releases = new TreeMap<Integer, Release>();
        initialize();
    }

    private void initialize() {
        File[] releaseDirectories = root.listFiles(DigitDirectoriesOnly.INSTANCE);
        if (releaseDirectories == null) {
            throw new IllegalArgumentException(root.getAbsolutePath() + " does not exist, so it cannot be used as a migration base directory");
        }
        for (File dir : releaseDirectories) {
            Release release = new Release(dir).initialize();
            releases.put(release.getNumber(), release);
        }
    }

    public Collection<Release> getReleases() {
        return new ArrayList<Release>(releases.values());
    }

    public int getMaxReleaseNumber() {
        return releases.lastKey();
    }

    public Release getRelease(int releaseNumber) {
        return releases.get(releaseNumber);
    }

    private static final class DigitDirectoriesOnly implements FileFilter {
        private static FileFilter INSTANCE = new DigitDirectoriesOnly();

        private Pattern firstCharIsDigit = Pattern.compile("^\\d");

        public boolean accept(File pathname) {
            return pathname.isDirectory() && firstCharIsDigit.matcher(pathname.getName()).find();
        }
    }
}
