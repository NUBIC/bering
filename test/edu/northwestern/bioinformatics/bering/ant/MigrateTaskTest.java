package edu.northwestern.bioinformatics.bering.ant;

import junit.framework.TestCase;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.BuildLogger;
import org.apache.tools.ant.BuildEvent;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

/**
 * @author Moses Hohman
 */
public class MigrateTaskTest extends TestCase {
    private MigrateTask task = new MigrateTask();
    private Project project = new Project();

    protected void setUp() throws Exception {
        project.addBuildListener(new ConsoleLogger());
        task.setProject(project);
    }

    public void testDefaultMigrationsDir() {
        assertEquals("db/migrate", task.getMigrationsDir());
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
