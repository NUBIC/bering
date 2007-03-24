package edu.northwestern.bioinformatics.bering.runtime.filesystem;

import edu.northwestern.bioinformatics.bering.runtime.Script;
import edu.northwestern.bioinformatics.bering.runtime.MigrationLoadingException;
import edu.northwestern.bioinformatics.bering.runtime.Release;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

/**
 * @author Rhett Sutphin
 */
class ScriptFactory {
    private File file;
    private Release release;

    public ScriptFactory(File file, Release release) {
        this.file = file;
        this.release = release;
    }

    public Script create() {
        String scriptText;
        try {
            scriptText = FileUtils.readFileToString(file);
        } catch (IOException e) {
            throw new MigrationLoadingException("Could not read " + file, e);
        }
        
        return new Script(FilenameUtils.getBaseName(file.getName()), scriptText, release);
    }
}
