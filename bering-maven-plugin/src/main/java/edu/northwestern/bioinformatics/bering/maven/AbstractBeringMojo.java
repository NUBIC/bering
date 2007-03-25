package edu.northwestern.bioinformatics.bering.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import com.pyx4j.log4j.MavenLogAppender;

/**
 * @author Rhett Sutphin
 */
public abstract class AbstractBeringMojo extends AbstractMojo {
    /**
     * @parameter expression="${project}"
     */
    private MavenProject project;

    /**
     * The base directory containing your numbered release directories.
     *
     * @required
     * @parameter expression="src/main/db/migrate"
     */
    private String migrationsDir;

    public final void execute() throws MojoExecutionException, MojoFailureException {
        // MavenLogAppender doesn't work in some cases
        // (when the logged message doesn't include an exception)
        
        // MavenLogAppender.startPluginLog(this);
        // try {
            executeInternal();
        // } finally {
        //    MavenLogAppender.endPluginLog(this);
        // }
    }

    protected abstract void executeInternal() throws MojoExecutionException, MojoFailureException;

    ////// CONFIGURATION

    public String getMigrationsDir() {
        return migrationsDir;
    }

    public void setMigrationsDir(String migrationsDir) {
        this.migrationsDir = migrationsDir;
    }

    public MavenProject getProject() {
        return project;
    }

    public void setProject(MavenProject project) {
        this.project = project;
    }
}
