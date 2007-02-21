package edu.northwestern.bioinformatics.bering.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import edu.northwestern.bioinformatics.bering.runtime.MigrateTaskHelper;
import edu.northwestern.bioinformatics.bering.runtime.BeringTaskException;

import java.sql.Connection;
import java.sql.SQLException;
import java.io.File;

/**
 * @goal migrate
 * Not sure about this:
 * @phase deploy
 */
public class MigrateMojo extends AbstractMojo {
    /**
     * The Bering dialect to use.  Must be the name of a class which implements
     * <code>edu.northwestern.bioinformatics.bering.dialect.Dialect</code>.
     *
     * @required
     * @parameter
     */
    private String dialect;

    /**
     * The base directory containing your numbered release directories.
     *
     * @required
     * @parameter expression="src/main/db/migrate"
     */
    private String migrationsDir;

    /**
     * For resolving relative <code>migrationsDir</code>s
     * @parameter expression="${basedir}
     */
    private String basedir;

    /**
     * The version of the database to which to migrate.  May be specified as "M|N" or "M-N" (where M
     * is the release number and N is the script number) or just "N" (in which case the maximum
     * release number will be used).  If not specified, will migrate to the the most recent
     * release and script.
     *
     * @parameter
     */
    private String targetVersion;

    /* TODO: there's probably a base class to be extracted from these parameters
     *  + getConnection
     */

    /**
     * JDBC URL to use
     * @required
     * @parameter
     */
    private String url;

    /**
     * Classname for JDBC driver
     * @required
     * @parameter
     */
    private String driver;

    /**
     * Username for database
     * @parameter
     */
    private String username;

    /**
     * Password for database
     * @parameter
     */
    private String password;

    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            MigrateTaskHelper helper = new MigrateTaskHelper(new MavenCallbacks());
            helper.setMigrationsDir(migrationsDir);
            helper.setTargetVersion(targetVersion);
            helper.setDialectName(dialect);
            helper.execute();
        } catch (BeringTaskException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    protected Connection getConnection() {
        try {
            return new DriverManagerDataSource(driver, url, username, password).getConnection();
        } catch (Exception e) {
            // Can't throw MojoExecutionException here b/c it's checked
            throw new BeringTaskException("Could not open database connection", e);
        }
    }

    private class MavenCallbacks implements MigrateTaskHelper.HelperCallbacks {
        public Connection getConnection() {
            return MigrateMojo.this.getConnection();
        }

        public File resolve(File f) {
            return new File(basedir, f.getName());
        }
    }
}
