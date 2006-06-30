package edu.northwestern.bioinformatics.bering.runtime;

import edu.northwestern.bioinformatics.bering.Migration;

import java.io.File;
import java.io.IOException;

import groovy.lang.GroovyClassLoader;
import org.codehaus.groovy.control.CompilationFailedException;

/**
 * @author rsutphin
 */
public class Script extends MigrationFile {
    private Release release;

    private static GroovyClassLoader loader;
    static {
        loader = new GroovyClassLoader(Script.class.getClassLoader());
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
        String className = getClassName();
        try {
            loader.parseClass(getFile());
            Class<?> migrationClass = Class.forName(className);
            if (Migration.class.isAssignableFrom(migrationClass)) {
                return (Class<? extends Migration>) migrationClass;
            } else {
                throw new MigrationLoadingException(migrationClass.getName() + " in " + getFile() + " does not extend " + Migration.class.getName());
            }
        } catch (IOException e) {
            throw new MigrationLoadingException("Could not read " + getFile(), e);
        } catch (CompilationFailedException e) {
            throw new MigrationLoadingException("Compiling " + getFile() + " failed", e);
        } catch (ClassNotFoundException e) {
            throw new MigrationLoadingException("Parsing " + getFile() + " succeeded, but could not load a class named " + className, e);
        }
    }

    public Release getRelease() {
        return release;
    }

    public Migration createMigrationInstance() {
        try {
            return loadClass().newInstance();
        } catch (InstantiationException e) {
            throw new MigrationLoadingException(e);
        } catch (IllegalAccessException e) {
            throw new MigrationLoadingException(e);
        }
    }

    public void up() {
        createMigrationInstance().up();
    }

    public void down() {
        createMigrationInstance().down();
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
