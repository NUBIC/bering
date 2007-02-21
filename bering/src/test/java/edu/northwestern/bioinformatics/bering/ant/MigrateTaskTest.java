package edu.northwestern.bioinformatics.bering.ant;

import junit.framework.TestCase;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.BuildLogger;
import org.apache.tools.ant.BuildEvent;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import edu.northwestern.bioinformatics.bering.dialect.AbstractDialect;
import edu.northwestern.bioinformatics.bering.dialect.Dialect;
import edu.northwestern.bioinformatics.bering.dialect.Oracle;

/**
 * @author Moses Hohman
 */
public class MigrateTaskTest extends TestCase {
    private MigrateTask task = new MigrateTask();
    private Project project = new Project();

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        project.addBuildListener(new ConsoleLogger());
        task.setProject(project);
    }

    public void testDefaultMigrationsDir() {
        assertEquals("db/migrate", task.getMigrationsDir());
    }

    private static class ConsoleLogger implements BuildLogger {
        public void setMessageOutputLevel(int level) { }

        public void setOutputPrintStream(PrintStream out) { }

        public void setEmacsMode(boolean emacsMode) { }

        public void setErrorPrintStream(PrintStream error) { }

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
