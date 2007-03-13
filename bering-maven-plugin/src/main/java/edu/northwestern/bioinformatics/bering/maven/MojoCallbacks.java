package edu.northwestern.bioinformatics.bering.maven;

import edu.northwestern.bioinformatics.bering.runtime.MigrateTaskHelper;
import edu.northwestern.bioinformatics.bering.runtime.BeringTaskException;

import java.sql.Connection;
import java.io.File;

import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * @author Rhett Sutphin
 */
class MojoCallbacks implements MigrateTaskHelper.HelperCallbacks {
    private File basedir;
    private DataSource ds;

    public MojoCallbacks(File basedir, String driver, String url, String username, String password) {
        this.basedir = basedir;
        this.ds = new SingleConnectionDataSource(driver, url, username, password, true);
    }

    public MojoCallbacks(File basedir, DataSource ds) {
        this.basedir = basedir;
        this.ds = ds;
    }

    public Connection getConnection() {
        try {
            return getDataSource().getConnection();
        } catch (Exception e) {
            // Can't throw MojoExecutionException here b/c it's checked
            throw new BeringTaskException("Could not open database connection", e);
        }
    }

    public JdbcTemplate getJdbcTemplate() {
        return new JdbcTemplate(getDataSource());
    }

    // Package-level for testing
    DataSource getDataSource() {
        return ds;
    }

    public File resolve(File f) {
        return new File(basedir, f.getPath());
    }
}
