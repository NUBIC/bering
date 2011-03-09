package edu.northwestern.bioinformatics.bering.servlet;

import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.easymock.classextension.EasyMock;
import static edu.northwestern.bioinformatics.bering.servlet.SeparateApplicationContextBeringContextListener.*;

/**
 * @author Rhett Sutphin
 */
public class SeparateApplicationContextBeringContextListenerTest extends ContextListenerTestCase {
    private SeparateApplicationContextBeringContextListener listener;
    private ConfigurableWebApplicationContext mockApplicationContext;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mockApplicationContext = registerMockFor(ConfigurableWebApplicationContext.class);

        listener = new SeparateApplicationContextBeringContextListener() {
            @Override
            protected ConfigurableWebApplicationContext createEmptyApplicationContext() {
                return mockApplicationContext;
            }
        };
    }

    public void testConfigLocationApplied() throws Exception {
        servletContext.addInitParameter(CONFIG_LOCATION_PARAM,
            "classpath:abc.xml;file:local.xml\n\nclasspath*:org/**/app*.xml");

        mockApplicationContext.setServletContext(servletContext);
        mockApplicationContext.setConfigLocations(EasyMock.aryEq(new String[] { "classpath:abc.xml", "file:local.xml", "classpath*:org/**/app*.xml" }));
        mockApplicationContext.refresh();
        replayMocks();

        assertSame(mockApplicationContext, listener.getApplicationContext(servletContext));
        verifyMocks();
    }

    public void testCloseOnComplete() throws Exception {
        mockApplicationContext.setServletContext(servletContext);
        mockApplicationContext.refresh();
        mockApplicationContext.close();
        replayMocks();

        listener.getApplicationContext(servletContext);
        listener.onMigrationComplete();
        verifyMocks();
    }
}
