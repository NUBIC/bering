package edu.northwestern.bioinformatics.bering.runtime.filesystem;

import edu.northwestern.bioinformatics.bering.runtime.MigrationFinder;
import edu.northwestern.bioinformatics.bering.runtime.Release;
import edu.northwestern.bioinformatics.bering.runtime.AbstractMigrationFinder;

import java.io.File;
import java.io.FileFilter;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Collection;
import java.util.regex.Pattern;

/**
 * @author Rhett Sutphin
 */
public class FilesystemMigrationFinder extends AbstractMigrationFinder {
    private File root;

    public FilesystemMigrationFinder(File root) {
        this.root = root;
        initialize();
    }

    private void initialize() {
        // allow nulls for testing
        if (root != null) {
            File[] releaseDirectories = root.listFiles(DigitDirectoriesOnly.INSTANCE);
            if (releaseDirectories == null) {
                throw new IllegalArgumentException(root.getAbsolutePath() + " does not exist, so it cannot be used as a migration base directory");
            }
            for (File dir : releaseDirectories) {
                addRelease(createRelease(dir));
            }
        }
    }

    private Release createRelease(File dir) {
        return new ReleaseFactory(dir).create();
    }

    private static final class DigitDirectoriesOnly implements FileFilter {
        private static FileFilter INSTANCE = new DigitDirectoriesOnly();

        private Pattern firstCharIsDigit = Pattern.compile("^\\d");

        public boolean accept(File pathname) {
            return pathname.isDirectory() && firstCharIsDigit.matcher(pathname.getName()).find();
        }
    }
}
