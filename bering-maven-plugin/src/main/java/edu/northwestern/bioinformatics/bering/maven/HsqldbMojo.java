package edu.northwestern.bioinformatics.bering.maven;

import edu.northwestern.bioinformatics.bering.runtime.BeringTaskException;
import edu.northwestern.bioinformatics.bering.runtime.MigrateTaskHelper;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.springframework.dao.DataAccessException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * Creates a read-only HSQLDB instance from the project's migrations.  The URL for the created
 * database is <code>jdbc:hsqldb:file:${outputDirectory}/${dbName}</code>, with username
 * <kbd>sa</kbd> and no password.
 *
 * @author Rhett Sutphin
 * @goal hsqldb
 */
public class HsqldbMojo extends AbstractMojo {
    /**
     * @parameter expression="${project}"
     */
    private MavenProject project;

    /**
     * @parameter expression="${basedir}"
     */
    private File basedir;

    /**
     * The base directory containing your numbered release directories.
     *
     * @required
     * @parameter expression="src/main/db/migrate"
     */
    private String migrationsDir;

    /**
     * The directory in which the HSQLDB schema, etc., files will be created.
     *
     * @parameter expression="${project.build.directory}/hsqldb"
     */
    private File outputDirectory;

    /**
     * @parameter expression="test"
     */
    private String dbName;

    public void execute() throws MojoExecutionException, MojoFailureException {
        clean(outputDirectory);

        String writableUrl = String.format("jdbc:hsqldb:file:%s/%s",
            outputDirectory.getAbsolutePath(), dbName);
        executeMigrations(writableUrl);

        File propfile = new File(outputDirectory, dbName + ".properties");
        markReadOnly(propfile);
    }

    private void markReadOnly(File propfile) throws MojoExecutionException {
        try {
            Writer w = new FileWriter(propfile, true);
            w.append("hsqldb.files_readonly=true\n");
            w.close();
        } catch (IOException e) {
            throw new MojoExecutionException("Marking database read-only failed", e);
        }
    }

    private void clean(File target) throws MojoExecutionException, MojoFailureException {
        getLog().debug("Cleaning out existing " + target.getAbsolutePath() + ", if any");
        try {
            if (target.exists()) FileUtils.deleteDirectory(target);
        } catch (IOException e) {
            throw new MojoExecutionException("Exception when cleaning " + target.getAbsolutePath(), e);
        }

        getLog().debug("Creating clean " + target.getAbsolutePath());
        if (!target.mkdirs()) throw new MojoFailureException("Could not create " + target.getAbsolutePath());
    }

    private void executeMigrations(String url) throws MojoExecutionException {
        getLog().debug("Attempting to execute all migrations against clean database at " + url);
        MojoCallbacks callbacks = new MojoCallbacks(basedir, org.hsqldb.jdbcDriver.class.getName(), url, "sa", "");
        try {
            MigrateTaskHelper helper = new MigrateTaskHelper(callbacks);
            helper.setDialectName(edu.northwestern.bioinformatics.bering.dialect.Hsqldb.class.getName());
            helper.setMigrationsDir(migrationsDir);
            helper.execute();
        } catch (BeringTaskException bte) {
            throw new MojoExecutionException("Running migrations on " + url + " failed", bte);
        }

        try {
            getLog().debug("Shutting down r/w connection to database");
            callbacks.getJdbcTemplate().execute("SHUTDOWN SCRIPT");
        } catch (DataAccessException e) {
            throw new MojoExecutionException("Database interaction failed during shutdown", e);
        }
    }
}
