package edu.northwestern.bioinformatics.bering.runtime;

import edu.northwestern.bioinformatics.bering.Migration;
import edu.northwestern.bioinformatics.bering.Adapter;
import groovy.lang.GroovyClassLoader;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilerConfiguration;

import java.io.File;
import java.io.IOException;

/**
 * @author Rhett Sutphin
 * @author Moses Hohman
 */
public class Script extends MigrationFile {
    private Release release;

    private static GroovyClassLoader loader;
    static {
        CompilerConfiguration compilerConfiguration = new CompilerConfiguration();
        loader = new GroovyClassLoader(Script.class.getClassLoader(), compilerConfiguration);
    }

    public Script(File file, Release release) {
        super(file);
        if (getName() == null) {
            throw new IllegalArgumentException("A name is required for scripts: " + file.getPath());
        }
        this.release = release;
    }

    public String getClassName() {
        String[] segments = getName().split("_");
        StringBuffer clsName = new StringBuffer();
        for (String segment : segments) {
            clsName.append(segment.substring(0, 1).toUpperCase());
            clsName.append(segment.substring(1));
        }
        return clsName.toString();
    }

    public Class<? extends Migration> loadClass() {
        try {
            Class<?> migrationClass = loader.parseClass(getFile());
            if (Migration.class.isAssignableFrom(migrationClass)) {
                return (Class<? extends Migration>) migrationClass;
            } else {
                throw new MigrationLoadingException(migrationClass.getName() + " in " + getFile() + " does not extend " + Migration.class.getName());
            }
        } catch (IOException e) {
            throw new MigrationLoadingException("Could not read " + getFile(), e);
        } catch (CompilationFailedException e) {
            throw new MigrationLoadingException("Compiling " + getFile() + " failed", e);
        }
    }

    public Release getRelease() {
        return release;
    }

    public Migration createMigrationInstance(Adapter adapter) {
        try {
            Migration instance = loadClass().newInstance();
            instance.setAdapter(adapter);
            return instance;
        } catch (InstantiationException e) {
            throw new MigrationLoadingException(e);
        } catch (IllegalAccessException e) {
            throw new MigrationLoadingException(e);
        }
    }

    public void up(Adapter adapter) {
        createMigrationInstance(adapter).up();
    }

    public void down(Adapter adapter) {
        createMigrationInstance(adapter).down();
    }

    public String toString() {
        return new StringBuilder()
            .append(getClass().getSimpleName()).append('[')
            .append(getRelease().getNumber()).append('-')
            .append(getNumber()).append(' ')
            .append(getName()).append(']')
            .toString();
    }
}
