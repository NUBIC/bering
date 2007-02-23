package edu.northwestern.bioinformatics.bering.maven;

import edu.northwestern.bioinformatics.bering.runtime.MigrateTaskHelper;
import edu.northwestern.bioinformatics.bering.runtime.BeringTaskException;

import java.sql.Connection;
import java.io.File;

import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author Rhett Sutphin
 */
class MojoCallbacks implements MigrateTaskHelper.HelperCallbacks {
    private File basedir;
    private String driver, url, username, password;
    private SingleConnectionDataSource ds;

    public MojoCallbacks(File basedir, String driver, String url, String username, String password) {
        this.basedir = basedir;
        this.driver = driver;
        this.url = url;
        this.username = username;
        this.password = password;
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

    private SingleConnectionDataSource getDataSource() {
        if (ds == null) ds = new SingleConnectionDataSource(driver, url, username, password, true);
        return ds;
    }

    public File resolve(File f) {
        return new File(basedir, f.getPath());
    }
}
