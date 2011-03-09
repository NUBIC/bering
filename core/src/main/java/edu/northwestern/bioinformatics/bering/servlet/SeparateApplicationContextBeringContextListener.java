package edu.northwestern.bioinformatics.bering.servlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;

import javax.servlet.ServletContext;

/**
 * Similar to {@link SpringBeringContextListener}, except that it uses its own application context.
 * The application context must be specified by the <code>beringContextConfigLocation</code>
 * initialization parameter, which has the same format as Spring's
 * <code>contextConfigLocation</code>.
 * <p>
 * Unlike {@link SpringBeringContextListener}, this listener should be configured to run
 * <i>before</i> {@link org.springframework.web.context.ContextLoaderListener}.
 *
 * @author Rhett Sutphin
 */
public class SeparateApplicationContextBeringContextListener extends SpringBeringContextListener {
    public static final String CONFIG_LOCATION_PARAM = "beringContextConfigLocation";

    private static final Log log = LogFactory.getLog(SeparateApplicationContextBeringContextListener.class);

    private ConfigurableWebApplicationContext localApplicationContext;

    @Override
    protected synchronized ApplicationContext getApplicationContext(ServletContext servletContext) {
        if (localApplicationContext == null) {
            try {
                localApplicationContext = createApplicationContext(servletContext);
            } catch (RuntimeException re) {
                log.error("Could not load application context for bering listener", re);
                throw re;
            } catch (Error error) {
                log.error("Could not load application context for bering listener", error);
                throw error;
            }
        }
        return localApplicationContext;
    }

    @Override
    protected void onMigrationComplete() {
        if (localApplicationContext != null) {
            log.info("Closing local application context for " + getClass().getSimpleName());
            localApplicationContext.close();
        }
    }

    /** This code is derived from {@link org.springframework.web.context.ContextLoader}. */
    private ConfigurableWebApplicationContext createApplicationContext(ServletContext servletContext) {
        log.info("Loading local application context for " + getClass().getSimpleName());
        ConfigurableWebApplicationContext wac = createEmptyApplicationContext();
        wac.setServletContext(servletContext);
        String configLocation = servletContext.getInitParameter(CONFIG_LOCATION_PARAM);
        if (configLocation != null) {
            wac.setConfigLocations(StringUtils.tokenizeToStringArray(configLocation,
                    ConfigurableWebApplicationContext.CONFIG_LOCATION_DELIMITERS));
        }

        wac.refresh();
        return wac;
    }

    // for testing
    protected ConfigurableWebApplicationContext createEmptyApplicationContext() {
        return new XmlWebApplicationContext();
    }
}
