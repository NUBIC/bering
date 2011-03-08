package edu.northwestern.bioinformatics.bering.servlet;

import edu.northwestern.bioinformatics.bering.BeringTestCase;
import static org.easymock.EasyMock.expect;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContextEvent;

/**
 * @author Rhett Sutphin
 */
public class SpringBeringContextListenerTest extends ContextListenerTestCase {
    private SpringBeringContextListener listener;

    private WebApplicationContext applicationContext;
    private DeployedMigrator migrator;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        listener = new SpringBeringContextListener();

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
