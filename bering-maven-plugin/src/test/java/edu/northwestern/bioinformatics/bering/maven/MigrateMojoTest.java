package edu.northwestern.bioinformatics.bering.maven;

import edu.northwestern.bioinformatics.bering.DataSourceProvider;
import edu.northwestern.bioinformatics.bering.runtime.MigrateTaskHelper;
import junit.framework.TestCase;
import static org.easymock.classextension.EasyMock.*;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.BadSqlGrammarException;
import org.apache.maven.plugin.MojoExecutionException;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author Rhett Sutphin
 */
public class MigrateMojoTest extends TestCase {
    private static final String URL = "jdbc:db:url";
    private static final String DRIVER_CLASS_NAME = Integer.class.getName(); // just has to be a real class
    private static final String USERNAME = "joe";
    private static final String PASSWORD = "zammen";

    private TestableMojo mojo;
    private MigrateTaskHelper helper;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        helper = createMock(MigrateTaskHelper.class);
        mojo = new TestableMojo();
    }

    private void expectExecute() {
        expectNullParameters();

        helper.execute();
        replay(helper);
    }

    private void expectNullParameters() {
        // default to expecting null for parameter setters
        helper.setDialectName(null);
        expectLastCall().anyTimes();
        helper.setMigrationsDir(null);
        expectLastCall().anyTimes();
        helper.setTargetVersion(null);
        expectLastCall().anyTimes();
    }

    public void testDataSourceProviderUsedIfSet() throws Exception {
        mojo.setVerifier(new CallbackVerifier() {
            public void verify(MojoCallbacks callbacks) {
                assertEquals("Wrong data source type", StubDataSource.class, callbacks.getDataSource().getClass());
            }
        });

        mojo.setDataSourceProvider(TestDataSourceProvider.class.getName());

        expectExecute();
        mojo.execute();
        verify(helper);
    }
    
    public void testJdbcPropsUsedIfDataSourceProviderNotSet() throws Exception {
        mojo.setVerifier(new CallbackVerifier() {
            public void verify(MojoCallbacks callbacks) {
                assertEquals("Wrong dataSource type", SingleConnectionDataSource.class,
                    callbacks.getDataSource().getClass());
                SingleConnectionDataSource actual = (SingleConnectionDataSource) callbacks.getDataSource();
                assertEquals("Wrong URL", URL, actual.getUrl());
                assertEquals("Wrong driver", DRIVER_CLASS_NAME, actual.getDriverClassName());
                assertEquals("Wrong username", USERNAME, actual.getUsername());
                assertEquals("Wrong password", PASSWORD, actual.getPassword());
            }
        });

        mojo.setDataSourceProvider(null);
        mojo.setUrl(URL);
        mojo.setDriver(DRIVER_CLASS_NAME);
        mojo.setUsername(USERNAME);
        mojo.setPassword(PASSWORD);

        expectExecute();
        mojo.execute();
        verify(helper);
    }

    public void testDataSourceProviderReceivesProperties() throws Exception {
        final String expectedName = "special";
        final Integer expectedSerial = 145;

        Properties props = new Properties();
        props.setProperty("name", expectedName);
        props.setProperty("serial", expectedSerial.toString());
        mojo.setDataSourceProviderProperties(props);
        mojo.setDataSourceProvider(TestDataSourceProvider.class.getName());

        mojo.setVerifier(new CallbackVerifier() {
            public void verify(MojoCallbacks callbacks) {
                assertEquals("Wrong data source type", StubDataSource.class, callbacks.getDataSource().getClass());
                StubDataSource actual = ((StubDataSource) callbacks.getDataSource());
                assertEquals("String property not set on data source", expectedName, actual.getName());
                assertEquals("Integer property not set on data source", expectedSerial, actual.getSerial());
            }
        });

        expectExecute();
        mojo.execute();
        verify(helper);
    }

    public void testBadSqlGrammarRethrownAsMojoException() throws Exception {
        mojo.setDataSourceProvider(TestDataSourceProvider.class.getName());
        BadSqlGrammarException expected = new BadSqlGrammarException("It's happening again", null, null);
        expectNullParameters();
        helper.execute();
        expectLastCall().andThrow(expected);

        replay(helper);
        try {
            mojo.execute();
            fail("Exception not thrown");
        } catch (MojoExecutionException mee) {
            assertEquals(expected.getMessage(), mee.getMessage());
            assertSame(expected, mee.getCause());
        }
    }

    private class TestableMojo extends MigrateMojo {
        private CallbackVerifier verifier;

        @Override
        protected MigrateTaskHelper createHelper(MojoCallbacks callbacks) {
            if (verifier != null) verifier.verify(callbacks);
            return helper;
        }

        public void setVerifier(CallbackVerifier verifier) {
            this.verifier = verifier;
        }
    }

    private static interface CallbackVerifier {
        void verify(MojoCallbacks callbacks);
    }

    public static class TestDataSourceProvider implements DataSourceProvider {
        private String name;
        private Integer serial;

        public DataSource getDataSource() {
            return new StubDataSource(name, serial);
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getSerial() {
            return serial;
        }

        public void setSerial(Integer serial) {
            this.serial = serial;
        }
    }

    public static class StubDataSource implements DataSource {
        private String name;
        private Integer serial;

        public StubDataSource(String s, Integer serial) {
            this.name = s;
            this.serial = serial;
        }

        public String getName() {
            return name;
        }

        public Integer getSerial() {
            return serial;
        }

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
