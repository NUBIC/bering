package edu.northwestern.bioinformatics.bering.servlet;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.dao.DataAccessException;
import org.springframework.beans.factory.annotation.Required;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import edu.northwestern.bioinformatics.bering.dialect.Dialect;
import edu.northwestern.bioinformatics.bering.runtime.classpath.ClasspathMigrationFinder;
import edu.northwestern.bioinformatics.bering.DatabaseAdapter;
import edu.northwestern.bioinformatics.bering.Main;

/**
 * In conjunction with {@link edu.northwestern.bioinformatics.bering.servlet.BeringContextListener},
 * provides a mechanism whereby database schema updates can be applied automatically on application
 * startup.
 *
 * @author Rhett Sutphin
 */
public class DeployedMigrator {
    public final static String DEFAULT_RESOURCE_PATH = "db/migrate";

    private String resourcePath = DEFAULT_RESOURCE_PATH;
    private JdbcTemplate jdbcTemplate;
    private DataSource dataSource;
    private Dialect dialect;

    public void migrate() {
        getJdbcTemplate().execute(new ConnectionCallback() {
            public Object doInConnection(Connection connection) throws SQLException, DataAccessException {
                migrate(connection);
                return null;
            }
        });
    }

    private void migrate(Connection connection) {
        DatabaseAdapter adapter = new DatabaseAdapter(connection, getDialect());
        Main main = new Main();
        main.setAdapter(adapter);
        main.setFinder(new ClasspathMigrationFinder(getResourcePath()));
        main.migrate(null, null);
    }

    ////// CONFIGURATION

    public JdbcTemplate getJdbcTemplate() {
        if (jdbcTemplate == null) jdbcTemplate = new JdbcTemplate(getDataSource());
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Dialect getDialect() {
        return dialect;
    }

    public void setDialect(Dialect dialect) {
        this.dialect = dialect;
    }

    @Required
    public String getResourcePath() {
        return resourcePath;
    }

    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }
}
