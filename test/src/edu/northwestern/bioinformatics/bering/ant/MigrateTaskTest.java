package edu.northwestern.bioinformatics.bering.ant;

import junit.framework.TestCase;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.BuildLogger;
import org.apache.tools.ant.BuildEvent;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import com.sun.java_cup.internal.version;
import edu.northwestern.bioinformatics.bering.dialect.Generic;
import edu.northwestern.bioinformatics.bering.dialect.Dialect;

/**
 * @author Moses Hohman
 */
public class MigrateTaskTest extends TestCase {
    private MigrateTask task = new MigrateTask();
    private Project project = new Project();

    protected void setUp() throws Exception {
        super.setUp();
        project.addBuildListener(new ConsoleLogger());
        task.setProject(project);
    }

    public void testDefaultMigrationsDir() {
        assertEquals("db/migrate", task.getMigrationsDir());
    }

    public void testDefaultDialect() throws Exception {
        assertEquals(Generic.class.getName(), task.getDialect());
    }
    
    public void testSetDialectBlankDoesNotChangeIt() throws Exception {
        task.setDialect("");
        assertEquals(Generic.class.getName(), task.getDialect());
    }

    public void testBuildExceptionThrownIfMigrationsDirIsNotADirectory() throws IOException {
        File tempFile = File.createTempFile("MigrateTaskTest", ".tmp");
        try {
            task.getProject().setBaseDir(tempFile.getParentFile());
            task.setMigrationsDir(tempFile.getName());
            task.execute();
            fail("Should have thrown BuildException");
        } catch (BuildException expected) {
            String expectedMessage = tempFile.getCanonicalPath() + " is not a directory";
            assertTrue("exception message does not start with \"" + expectedMessage + "\": "  + expected.getMessage(),
                    expected.getMessage().startsWith(expectedMessage));
        }
    }

    public void testSetTargetVersion() throws Exception {
        assertTargetVersionSet(5, 9, "5|9");
        assertTargetVersionSet(null, 11, "11");
        assertTargetVersionSet(null, null, "");
    }

    public void testSetInvalidTargetVersion() throws Exception {
        try {
            task.setTargetVersion("bad");
            fail("Exception not thrown");
        } catch (BuildException be) {
            assertEquals("Invalid target version (bad).  Should have the form 'R|M' or 'M'.", be.getMessage());
            assertNotNull(be.getCause());
        }
    }

    public void testInvalidDialect() throws Exception {
        task.setDialect("this is not a class");
        try {
            task.createDialect();
            fail("Exception not thrown");
        } catch (BuildException e) {
            assertEquals("Could not find dialect class this is not a class", e.getMessage());
        }
    }

    public void testNonDialectDialect() throws Exception {
        task.setDialect(String.class.getName());
        try {
            task.createDialect();
            fail("Exception not thrown");
        } catch (BuildException e) {
            assertEquals("Class java.lang.String does not implement " + Dialect.class.getName(), e.getMessage());
        }
    }

    private void assertTargetVersionSet(Integer expectedTargetRelease, Integer expectedTargetMigration, String targetVersionString) {
        task.setTargetVersion(targetVersionString);
        assertEquals("Target release wrong", expectedTargetRelease, task.getTargetRelease());
        assertEquals("Target migration wrong", expectedTargetMigration, task.getTargetMigration());
    }

    private static class ConsoleLogger implements BuildLogger {
        public void setMessageOutputLevel(int level) {
        }

        public void setOutputPrintStream(PrintStream out) {
        }

        public void setEmacsMode(boolean emacsMode) {
        }

        public void setErrorPrintStream(PrintStream error) {
        }

        public void buildStarted(BuildEvent event) {
            messageLogged(event);
        }

        public void buildFinished(BuildEvent event) {
            messageLogged(event);
        }

        public void targetStarted(BuildEvent event) {
            messageLogged(event);
        }

        public void targetFinished(BuildEvent event) {
            messageLogged(event);
        }

        public void taskStarted(BuildEvent event) {
            messageLogged(event);
        }

        public void taskFinished(BuildEvent event) {
            messageLogged(event);
        }

        public void messageLogged(BuildEvent event) {
            System.out.println(event.getMessage());
        }
    }
}
