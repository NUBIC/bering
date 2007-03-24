package edu.northwestern.bioinformatics.bering.runtime.classpath;

import org.springframework.core.io.Resource;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import edu.northwestern.bioinformatics.bering.runtime.Release;
import edu.northwestern.bioinformatics.bering.runtime.MigrationLoadingException;
import edu.northwestern.bioinformatics.bering.runtime.Script;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * @author Rhett Sutphin
 */
public class ReleaseFactory {
    private ScriptResource[] scriptResources;
    private Map<String,Release> releasesByName;

    public ReleaseFactory(Resource[] resources) {
        this.scriptResources = new ScriptResource[resources.length];
        for (int i = 0; i < resources.length; i++) {
            scriptResources[i] = new ScriptResource(resources[i]);
        }
        releasesByName = new HashMap<String, Release>();
    }

    public Release[] createReleases() {
        for (ScriptResource scriptResource : scriptResources) {
            Release release = getRelease(scriptResource);
            release.addScript(
                createScript(scriptResource.getScriptName(), scriptResource.getResource(), release));
        }
        return new ArrayList<Release>(releasesByName.values()).toArray(new Release[releasesByName.size()]);
    }

    private Release getRelease(ScriptResource scriptResource) {
        if (!releasesByName.containsKey(scriptResource.getReleaseName())) {
            releasesByName.put(scriptResource.getReleaseName(), new Release(scriptResource.getReleaseName()));
        }
        return releasesByName.get(scriptResource.getReleaseName());
    }

    private Script createScript(String scriptName, Resource resource, Release release) {
        try {
            String scriptText = IOUtils.toString(resource.getInputStream());
            return new Script(scriptName, scriptText, release);
        } catch (IOException e) {
            throw new MigrationLoadingException("Could not read contents of resource " + resource, e);
        }
    }

    private static class ScriptResource {
        private Resource resource;
        private String releaseName;
        private String scriptName;

        public ScriptResource(Resource resource) {
            this.resource = resource;
            String[] urlComponents = getURL().split("/");
            releaseName = urlComponents[urlComponents.length - 2];
            scriptName = FilenameUtils.getBaseName(urlComponents[urlComponents.length - 1]);
        }

        private String getURL() {
            try {
                return resource.getURL().toString();
            } catch (IOException e) {
                throw new MigrationLoadingException("Could not get URL for " + resource, e);
            }
        }

        public Resource getResource() {
            return resource;
        }

        public String getReleaseName() {
            return releaseName;
        }

        public String getScriptName() {
            return scriptName;
        }
    }
}
