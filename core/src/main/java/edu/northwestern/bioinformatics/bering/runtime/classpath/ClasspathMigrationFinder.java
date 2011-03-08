package edu.northwestern.bioinformatics.bering.runtime.classpath;

import edu.northwestern.bioinformatics.bering.runtime.AbstractMigrationFinder;
import edu.northwestern.bioinformatics.bering.runtime.MigrationLoadingException;
import edu.northwestern.bioinformatics.bering.runtime.Release;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.Resource;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

/**
 * @author Rhett Sutphin
 */
public class ClasspathMigrationFinder extends AbstractMigrationFinder {
    private static final Log log = LogFactory.getLog(ClasspathMigrationFinder.class);

    private String rootResourcePath;
    private ResourcePatternResolver resolver;

    public ClasspathMigrationFinder(String rootResourcePath) {
        this.rootResourcePath = rootResourcePath;
        resolver = new PathMatchingResourcePatternResolver();
        initialize();
    }

    private void initialize() {
        String pattern = createPattern();
        try {
            Resource[] resources = getResources(pattern);
            Release[] releases = new ReleaseFactory(resources).createReleases();
            for (Release release : releases) {
                addRelease(release);
            }
        } catch (IOException e) {
            throw new MigrationLoadingException(
                "Could not find migration scripts using pattern " + pattern, e);
        }
    }

    private Resource[] getResources(String pattern) throws IOException {
        if (log.isDebugEnabled()) log.debug("Looking for resources matching " + pattern);
        Resource[] resources = resolver.getResources(pattern);
        if (resources.length == 0) {
            throw new MigrationLoadingException(
                "No migration scripts found using pattern " + pattern);
        }

        log.info(resources.length + " bering script resource(s) found matching " + pattern);
        if (log.isDebugEnabled()) {
            for (int i = 0; i < resources.length; i++) {
                log.debug(" " + i + ") " + resources[i]);
            }
        }
        return resources;
    }

    private String createPattern() {
        return String.format("classpath*:%s%s*/*.groovy",
            rootResourcePath,
            rootResourcePath.endsWith("/") ? "" : "/");
    }

}
