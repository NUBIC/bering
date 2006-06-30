package edu.northwestern.bioinformatics.bering.runtime;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author rsutphin
 */
public class Release extends MigrationFile {
    private SortedMap<Integer, Script> scripts;

    public Release(File directory) {
        super(directory);
        this.scripts = new TreeMap<Integer, Script>();
    }

    // This is separate from the constructor to aid testing
    public Release initialize() {
        for (File scriptFile : listScripts()) {
            Script script = new Script(scriptFile, this);
            addScript(script);
        }
        return this;
    }

    protected void addScript(Script script) {
        scripts.put(script.getNumber(), script);
    }

    private File[] listScripts() {
        File[] scriptFiles = getFile().listFiles(GroovyFilesOnly.INSTANCE);
        if (scriptFiles == null) {
            throw new IllegalArgumentException(getFile().getAbsolutePath() + " does not exist, so it can't be used as a release directory");
        }
        return scriptFiles;
    }

    public Collection<Script> getScripts() {
        return scripts.values();
    }

    public Script getScript(int number) {
        return scripts.get(number);
    }

    public Integer getMaxScriptNumber() {
        if (getScripts().size() == 0) return 0;
        return scripts.lastKey();
    }

    private static class GroovyFilesOnly implements FilenameFilter {
        public static final FilenameFilter INSTANCE = new GroovyFilesOnly();

        public boolean accept(File dir, String name) {
            return name.endsWith(".groovy");
        }
    }
}
