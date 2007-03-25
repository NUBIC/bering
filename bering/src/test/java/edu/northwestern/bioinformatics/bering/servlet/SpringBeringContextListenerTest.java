package edu.northwestern.bioinformatics.bering.servlet;

import static org.easymock.EasyMock.expect;
import edu.northwestern.bioinformatics.bering.BeringTestCase;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContext;

import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.easymock.classextension.EasyMock;
import static org.easymock.classextension.EasyMock.*;

/**
 * @author Rhett Sutphin
 */
public class SpringBeringContextListenerTest extends BeringTestCase {
    private SpringBeringContextListener listener;

    private MockServletContext servletContext;
    private ServletContextEvent event;
    private WebApplicationContext applicationContext;
    private DeployedMigrator migrator;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        listener = new SpringBeringContextListener();
        servletContext = new MockServletContext();
        event = new ServletContextEvent(servletContext);

        applicationContext = registerMockFor(WebApplicationContext.class);
        servletContext.setAttribute(
            WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE,
            applicationContext);

        migrator = registerMockFor(DeployedMigrator.class);
        migrator.migrate();
    }

    public void testDefaultBeanName() throws Exception {
        expect(applicationContext.getBean(SpringBeringContextListener.DEFAULT_MIGRATOR_BEAN_NAME))
            .andReturn(migrator);

        replayMocks();
        listener.contextInitialized(event);
        verifyMocks();
    }

    public void testBeanNameRetrievedFromServletContext() throws Exception {
        String expectedBeanName = "somethingElse";
        servletContext.addInitParameter(
            SpringBeringContextListener.BEAN_NAME_INIT_PARAMETER_NAME, expectedBeanName);

        expect(applicationContext.getBean(expectedBeanName)).andReturn(migrator);

        replayMocks();
        listener.contextInitialized(event);
        verifyMocks();
    }
}
