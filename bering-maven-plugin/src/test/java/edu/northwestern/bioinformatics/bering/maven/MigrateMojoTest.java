package edu.northwestern.bioinformatics.bering.maven;

import edu.northwestern.bioinformatics.bering.DataSourceProvider;
import edu.northwestern.bioinformatics.bering.runtime.MigrateTaskHelper;
import junit.framework.TestCase;
import static org.easymock.classextension.EasyMock.*;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Rhett Sutphin
 */
public class MigrateMojoTest extends TestCase {
    private static final String URL = "jdbc:db:url";
    private static final String DRIVER_CLASS_NAME = Integer.class.getName(); // just has to be a real class
    private static final String USERNAME = "joe";
    private static final String PASSWORD = "zammen";

    private MigrateTaskHelper helper;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        helper = createMock(MigrateTaskHelper.class);
    }

    private void expectExecute() {
        // default to expecting null for parameter setters
        helper.setDialectName(null);   expectLastCall().anyTimes();
        helper.setMigrationsDir(null); expectLastCall().anyTimes();
        helper.setTargetVersion(null); expectLastCall().anyTimes();
        helper.execute();
        replay(helper);
    }

    public void testDataSourceProviderUsedIfSet() throws Exception {
        MigrateMojo m = new MigrateMojo() {
            @Override MigrateTaskHelper createHelper(MojoCallbacks callbacks) {
                assertEquals("Wrong data source type", StubDataSource.class, callbacks.getDataSource().getClass());
                return helper;
            }
        };

        m.setDataSourceProvider(TestDataSourceProvider.class.getName());

        expectExecute();
        m.execute();
        verify(helper);
    }
    
    public void testJdbcPropsUsedIfDataSourceProviderNotSet() throws Exception {
        MigrateMojo m = new MigrateMojo() {
            @Override MigrateTaskHelper createHelper(MojoCallbacks callbacks) {
                assertEquals("Wrong dataSource type", SingleConnectionDataSource.class,
                    callbacks.getDataSource().getClass());
                SingleConnectionDataSource actual = (SingleConnectionDataSource) callbacks.getDataSource();
                assertEquals("Wrong URL", URL, actual.getUrl());
                assertEquals("Wrong driver", DRIVER_CLASS_NAME, actual.getDriverClassName());
                assertEquals("Wrong username", USERNAME, actual.getUsername());
                assertEquals("Wrong password", PASSWORD, actual.getPassword());
                return helper;
            }
        };

        m.setDataSourceProvider(null);
        m.setUrl(URL);
        m.setDriver(DRIVER_CLASS_NAME);
        m.setUsername(USERNAME);
        m.setPassword(PASSWORD);

        expectExecute();
        m.execute();
        verify(helper);
    }

    public static class TestDataSourceProvider implements DataSourceProvider {
        public DataSource getDataSource() {
            return new StubDataSource();
        }
    }

    public static class StubDataSource implements DataSource {
        public Connection getConnection() throws SQLException {
            throw new UnsupportedOperationException("getConnection not implemented");
        }

        public Connection getConnection(String username, String password) throws SQLException {
            throw new UnsupportedOperationException("getConnection not implemented");
        }

        public PrintWriter getLogWriter() throws SQLException {
            throw new UnsupportedOperationException("getLogWriter not implemented");
        }

        public void setLogWriter(PrintWriter out) throws SQLException {
            throw new UnsupportedOperationException("setLogWriter not implemented");
        }

        public void setLoginTimeout(int seconds) throws SQLException {
            throw new UnsupportedOperationException("setLoginTimeout not implemented");
        }

        public int getLoginTimeout() throws SQLException {
            throw new UnsupportedOperationException("getLoginTimeout not implemented");
        }
    }
}
