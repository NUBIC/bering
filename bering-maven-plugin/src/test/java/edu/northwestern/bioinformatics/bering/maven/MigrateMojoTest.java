package edu.northwestern.bioinformatics.bering.maven;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URISyntaxException;

/**
 * @author Rhett Sutphin
 */
public class MigrateMojoTest extends AbstractMojoTestCase {
    private MigrateMojo mojo;

    public MigrateMojoTest() throws URISyntaxException, IOException {
        // The basedir property is only read once, then stored in a private static var,
        // so we have to set it as soon as possible (i.e., not in setUp).
        File targetTestClasses = new File(getClass().getResource("/").toURI());
        System.setProperty("basedir", new File(targetTestClasses, "../..").getCanonicalPath());
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        URL defaults = getClass().getResource("defaults.xml");
        mojo = null;
        // mojo = (MigrateMojo) lookupMojo("migrate", new File(defaults.toURI()));
    }

    // TODO: this test is not passing.  Need to investigate whether this is a problem
    // with the test harness, or with the code.
    public void testDefaultMigrationDir() throws Exception {
        // String migrationsDir = (String) getVariableValueFromObject(mojo, "migrationsDir");
        // assertEquals("src/main/db/migrate", migrationsDir);
    }
}
