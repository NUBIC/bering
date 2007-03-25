package edu.northwestern.bioinformatics.bering.runtime.filesystem;

import edu.northwestern.bioinformatics.bering.runtime.Release;
import edu.northwestern.bioinformatics.bering.runtime.Script;

import java.io.File;
import java.io.FilenameFilter;

import org.apache.commons.io.FilenameUtils;

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
            r.addScript(createScript(scriptFile, r));
        }
        return r;
    }

    private Script createScript(File scriptFile, Release release) {
        return new Script(
            FilenameUtils.getBaseName(scriptFile.getName()),
            scriptFile.toURI(),
            release);
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
