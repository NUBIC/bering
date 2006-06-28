package edu.northwestern.bioinformatics.bering.runtime;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author rsutphin
 */
public class Release extends MigrationFile {
    private List<Script> scripts;

    public Release(File directory) {
        super(directory);
        this.scripts = new LinkedList<Script>();
    }

    // This is separate from the constructor to aid testing
    public Release initialize() {
        for (File scriptFile : listScripts()) {
            scripts.add(new Script(scriptFile));
        }
        Collections.sort(scripts);
        return this;
    }

    private File[] listScripts() {
        File[] scriptFiles = getFile().listFiles(GroovyFilesOnly.INSTANCE);
        if (scriptFiles == null) {
            throw new IllegalArgumentException(getFile().getAbsolutePath() + " does not exist, so it can't be used as a release directory");
        }
        return scriptFiles;
    }

    public List<Script> getScripts() {
        return scripts;
    }

    private static class GroovyFilesOnly implements FilenameFilter {
        public static final FilenameFilter INSTANCE = new GroovyFilesOnly();

        public boolean accept(File dir, String name) {
            return name.endsWith(".groovy");
        }
    }
}
