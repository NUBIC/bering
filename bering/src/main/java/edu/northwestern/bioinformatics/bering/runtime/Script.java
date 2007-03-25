package edu.northwestern.bioinformatics.bering.runtime;

import edu.northwestern.bioinformatics.bering.Adapter;
import edu.northwestern.bioinformatics.bering.Migration;
import groovy.lang.GroovyClassLoader;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.apache.commons.io.IOUtils;

import java.net.URI;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.IOException;

/**
 * @author Rhett Sutphin
 * @author Moses Hohman
 */
public class Script extends MigrationElement {
    private URI uri;
    private Release release;

    private static GroovyClassLoader loader;
    static {
        CompilerConfiguration compilerConfiguration = new CompilerConfiguration();
        loader = new GroovyClassLoader(Script.class.getClassLoader(), compilerConfiguration);
    }

    public Script(String scriptName, URI uri, Release release) {
        super(scriptName);
        if (getName() == null) {
            throw new MigrationLoadingException("A name is required for scripts: " + getElementName());
        }
        this.release = release;
        this.uri = uri;
    }

    public String getScriptText() {
        URL url = null;
        try {
            url = getUri().toURL();
            return IOUtils.toString(url.openStream());
        } catch (MalformedURLException e) {
            throw new MigrationLoadingException(
                "Script URI is not convertable to a URL: " + getUri(), e);
        } catch (IOException e) {
            throw new MigrationLoadingException("Could not read script from " + url, e);
        }
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

    @SuppressWarnings("unchecked")
    public Class<? extends Migration> loadClass() {
        try {
            Class<?> migrationClass = loader.parseClass(getScriptText(), getElementName());
            if (Migration.class.isAssignableFrom(migrationClass)) {
                return (Class<? extends Migration>) migrationClass;
            } else {
                throw new MigrationLoadingException(migrationClass.getName() + " in " + getElementName() + " does not extend " + Migration.class.getName());
            }
        } catch (CompilationFailedException e) {
            throw new MigrationLoadingException("Compiling " + getElementName() + " failed", e);
        }
    }

    public URI getUri() {
        return uri;
    }

    public Release getRelease() {
        return release;
    }

    public Migration createMigrationInstance(Adapter adapter) {
        try {
            Migration instance = loadClass().newInstance();
            instance.setAdapter(adapter);
            instance.setSourceUri(getUri());
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

    public String getNumericDesignator() {
        return new StringBuilder()
            .append(getRelease().getNumber()).append('|').append(getNumber())
            .toString();
    }

    @Override
    public String toString() {
        return new StringBuilder()
            .append(getClass().getSimpleName()).append('[')
            .append(getNumericDesignator()).append(' ')
            .append(getName()).append(']')
            .toString();
    }
}
