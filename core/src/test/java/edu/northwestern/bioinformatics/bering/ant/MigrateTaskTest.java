package edu.northwestern.bioinformatics.bering.ant;

import junit.framework.TestCase;
import org.apache.tools.ant.Project;
import static org.easymock.classextension.EasyMock.*;

import java.io.File;

/**
 * @author Moses Hohman
 */
public class MigrateTaskTest extends TestCase {
    private static final File BASE_DIR = new File("/foo/bar");

    private MigrateTask task = new MigrateTask();
    private Project project;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        project = createMock(Project.class);
        task.setProject(project);
        expect(project.getBaseDir()).andReturn(BASE_DIR).anyTimes();
        replay(project);
    }

    public void testDefaultMigrationsDir() {
        assertEquals("db/migrate", task.getMigrationsDir());
    }

    public void testResolvedDefaultMigrationsDir() throws Exception {
        File f = new File(task.getMigrationsDir());
        assertEquals(BASE_DIR + "/db/migrate", task.createHelperCallbacks().resolve(f).getAbsolutePath());
    }
}
