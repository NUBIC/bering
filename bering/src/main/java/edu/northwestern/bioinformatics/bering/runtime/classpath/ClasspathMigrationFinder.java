package edu.northwestern.bioinformatics.bering.runtime.classpath;

import edu.northwestern.bioinformatics.bering.runtime.AbstractMigrationFinder;
import edu.northwestern.bioinformatics.bering.runtime.MigrationLoadingException;
import edu.northwestern.bioinformatics.bering.runtime.Release;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.Resource;
import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

/**
 * @author Rhett Sutphin
 */
public class ClasspathMigrationFinder extends AbstractMigrationFinder {
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
            Resource[] resources = resolver.getResources(pattern);
            Release[] releases = new ReleaseFactory(resources).createReleases();
            for (Release release : releases) {
                addRelease(release);
            }
        } catch (IOException e) {
            throw new MigrationLoadingException("Could not find migration scripts using pattern " + pattern, e);
        }
    }

    private String createPattern() {
        return String.format("classpath*:%s%s/*/*.groovy",
            rootResourcePath,
            rootResourcePath.endsWith("/") ? "" : "/");
    }

}
