package edu.northwestern.bioinformatics.bering.runtime.filesystem;

import edu.northwestern.bioinformatics.bering.runtime.Release;
import edu.northwestern.bioinformatics.bering.runtime.Script;

import java.io.File;
import java.io.FilenameFilter;

/**
 * @author Rhett Sutphin
*/
final class ReleaseFactory {
    private File directory;

    public ReleaseFactory(File directory) {
        this.directory = directory;
    }

    public Release create() {
        Release r = new Release(directory.getName());
        for (File scriptFile : listScripts()) {
            Script script = new ScriptFactory(scriptFile, r).create();
            r.addScript(script);
        }
        return r;
    }

    private File[] listScripts() {
        File[] scriptFiles = directory.listFiles(GroovyFilesOnly.INSTANCE);
        if (scriptFiles == null) {
            throw new IllegalArgumentException(directory.getAbsolutePath() + " does not exist, so it can't be used as a release directory");
        }
        return scriptFiles;
    }

    private static class GroovyFilesOnly implements FilenameFilter {
        public static final FilenameFilter INSTANCE = new GroovyFilesOnly();

        public boolean accept(File dir, String name) {
            return name.endsWith(".groovy");
        }
    }
}
