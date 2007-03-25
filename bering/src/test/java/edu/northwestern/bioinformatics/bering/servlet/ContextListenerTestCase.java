package edu.northwestern.bioinformatics.bering.servlet;

import edu.northwestern.bioinformatics.bering.BeringTestCase;
import org.springframework.mock.web.MockServletContext;

import javax.servlet.ServletContextEvent;

/**
 * @author Rhett Sutphin
 */
public abstract class ContextListenerTestCase extends BeringTestCase {
    protected MockServletContext servletContext;
    protected ServletContextEvent event;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        servletContext = new MockServletContext();
        event = new ServletContextEvent(servletContext);
    }
}
