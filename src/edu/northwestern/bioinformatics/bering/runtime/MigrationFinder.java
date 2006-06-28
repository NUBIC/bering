package edu.northwestern.bioinformatics.bering.runtime;

import java.io.File;
import java.io.FileFilter;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Pattern;

/**
 * @author rsutphin
 */
public class MigrationFinder {
    private File root;
    private List<Release> releases;

    public MigrationFinder(File root) {
        this.root = root;
        this.releases = new ArrayList<Release>();
        initialize();
    }

    private void initialize() {
        File[] releaseDirectories = root.listFiles(DigitDirectoriesOnly.INSTANCE);
        if (releaseDirectories == null) {
            throw new IllegalArgumentException(root.getAbsolutePath() + " does not exist, so it cannot be used as a migration base directory");
        }
        for (File dir : releaseDirectories) {
            releases.add(new Release(dir).initialize());
        }
        Collections.sort(releases);
    }

    public List<Release> getReleases() {
        return releases;
    }

    private static final class DigitDirectoriesOnly implements FileFilter {
        private static FileFilter INSTANCE = new DigitDirectoriesOnly();

        private Pattern firstCharIsDigit = Pattern.compile("^\\d");

        public boolean accept(File pathname) {
            return pathname.isDirectory() && firstCharIsDigit.matcher(pathname.getName()).find();
        }
    }
}
