package edu.northwestern.bioinformatics.bering.maven;

import edu.northwestern.bioinformatics.bering.BeringException;
import edu.northwestern.bioinformatics.bering.DataSourceProvider;
import edu.northwestern.bioinformatics.bering.runtime.MigrateTaskHelper;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import javax.sql.DataSource;
import java.io.File;
import java.util.Properties;

/**
 * Executes any outstanding migrations for the configured database.
 * <p>
 *
 *
 * @author Rhett Sutphin
 * @goal migrate
 */
public class MigrateMojo extends AbstractBeringMojo {
    /**
     * The Bering dialect to use.  Must be the name of a class which implements
     * <code>edu.northwestern.bioinformatics.bering.dialect.Dialect</code>.  If not provided,
     * Bering will guess based on the database it connects to.
     *
     * @parameter
     */
    private String dialect;

    /**
     * For resolving relative <code>migrationsDir</code>s
     * @parameter expression="${basedir}"
     */
    private File basedir;

    /**
     * The version of the database to which to migrate.  May be specified as "M|N" or "M-N" (where M
     * is the release number and N is the script number) or just "N" (in which case the maximum
     * release number will be used).  If not specified, will migrate to the the most recent
     * release and script.
     *
     * @parameter expression="${migrate.version}"
     */
    private String targetVersion;

    /**
     * Classname for a class implementing <code>edu.northwestern.bioinformatics.bering.DataSourceProvider</code>.
     * This class must have a public default constructor.  If this parameter
     * is provided, the JDBC connection parameters will be ignored.
     *
     * @parameter
     */
    private String dataSourceProvider;

    /**
     * Properties to apply to the dataSourceProvider after construction.  The dataSourceProvider
     * class must provide bean-style setters for all the properties given in this parameter.
     *
     * @parameter
     */
    private Properties dataSourceProviderProperties;

    /**
     * JDBC URL to use
     * @parameter
     */
    private String url;

    /**
     * Classname for JDBC driver
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

    private DataSource createDataSource() throws MojoExecutionException {
        if (dataSourceProvider != null) {
            return getDataSourceFromProvider();
        } else {
            return new SingleConnectionDataSource(driver, url, username, password, true);
        }
    }

    @SuppressWarnings("unchecked")
    private DataSource getDataSourceFromProvider() throws MojoExecutionException {
        Class<DataSourceProvider> clazz;
        try {
            clazz = (Class<DataSourceProvider>) Class.forName(dataSourceProvider);
        } catch (ClassNotFoundException e) {
            throw new MojoExecutionException("Could not find " + dataSourceProvider, e);
        }

        DataSourceProvider provider;
        try {
            provider = clazz.newInstance();
        } catch (InstantiationException e) {
            throw new MojoExecutionException("Could not instantiate " + clazz.getName()
                + "; does it have a public default constructor?", e);
        } catch (IllegalAccessException e) {
            throw new MojoExecutionException("Could not instantiate " + clazz.getName()
                + "; does it have a public default constructor?", e);
        }

        Properties props = getDataSourceProviderProperties();
        if (props != null) {
            BeanWrapper wrapper = new BeanWrapperImpl(provider);
            for (Object o : props.keySet()) {
                String propName = (String) o;
                wrapper.setPropertyValue(propName, props.get(propName));
            }
        }

        return provider.getDataSource();
    }

    @Override
    protected void executeInternal() throws MojoExecutionException, MojoFailureException {
        try {
            MojoCallbacks callbacks = new MojoCallbacks(basedir, createDataSource());
            MigrateTaskHelper helper = createHelper(callbacks);
            helper.setMigrationsDir(getMigrationsDir());
            helper.setTargetVersion(targetVersion);
            helper.setDialectName(dialect);
            helper.execute();
        } catch (BeringException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    protected MigrateTaskHelper createHelper(MojoCallbacks callbacks) {
        return new MigrateTaskHelper(callbacks);
    }

    ////// BEAN ACCESSORS (for testing)

    public String getDialect() {
        return dialect;
    }

    public void setDialect(String dialect) {
        this.dialect = dialect;
    }

    public File getBasedir() {
        return basedir;
    }

    public void setBasedir(File basedir) {
        this.basedir = basedir;
    }

    public String getTargetVersion() {
        return targetVersion;
    }

    public void setTargetVersion(String targetVersion) {
        this.targetVersion = targetVersion;
    }

    public String getDataSourceProvider() {
        return dataSourceProvider;
    }

    public void setDataSourceProvider(String dataSourceProvider) {
        this.dataSourceProvider = dataSourceProvider;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Properties getDataSourceProviderProperties() {
        return dataSourceProviderProperties;
    }

    public void setDataSourceProviderProperties(Properties properties) {
        this.dataSourceProviderProperties = properties;
    }
}
