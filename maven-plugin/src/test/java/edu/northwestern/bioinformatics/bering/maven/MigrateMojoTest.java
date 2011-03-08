package edu.northwestern.bioinformatics.bering.maven;

import edu.northwestern.bioinformatics.bering.DataSourceProvider;
import edu.northwestern.bioinformatics.bering.runtime.MigrateTaskHelper;
import junit.framework.TestCase;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import javax.sql.DataSource;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Properties;

import static org.easymock.classextension.EasyMock.*;

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
                assertTrue("Wrong data source type",
                    NamedDataSource.class.isAssignableFrom(callbacks.getDataSource().getClass()));
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
                NamedDataSource actual = ((NamedDataSource) callbacks.getDataSource());
                assertEquals("String property not set on data source", expectedName, actual.getName());
                assertEquals("Integer property not set on data source", expectedSerial, actual.getSerial());
            }
        });

        expectExecute();
        mojo.execute();
        verify(helper);
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

    private interface CallbackVerifier {
        void verify(MojoCallbacks callbacks);
    }

    public static class TestDataSourceProvider implements DataSourceProvider {
        private String name;
        private Integer serial;

        /**
         * Using a proxy rather than the preferable static stub implementation so that
         * this class can be compiled on both Java 5 and Java 6.
         */
        public DataSource getDataSource() {
            return (DataSource) Proxy.newProxyInstance(
                getClass().getClassLoader(), new Class[] { NamedDataSource.class },
                new StubDataSourceBehavior());
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

        private class StubDataSourceBehavior implements InvocationHandler {
            public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
                if ("getName".equals(method.getName())) {
                    return name;
                } else if ("getSerial".equals(method.getName())) {
                    return serial;
                } else {
                    throw new UnsupportedOperationException(method.getName() + " not implemented");
                }
            }
        }
    }

    private interface NamedDataSource extends DataSource {
        String getName();
        Integer getSerial();
    }
}
