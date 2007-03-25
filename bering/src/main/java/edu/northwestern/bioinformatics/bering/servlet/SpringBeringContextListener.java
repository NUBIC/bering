package edu.northwestern.bioinformatics.bering.servlet;

import edu.northwestern.bioinformatics.bering.servlet.DeployedMigrator;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;

/**
 * Implementation of {@link BeringContextListener} which retrieves the {@link DeployedMigrator}
 * from the configured {@link org.springframework.web.context.WebApplicationContext}. It must be
 * configured to run after {@link org.springframework.web.context.ContextLoaderListener} in the
 * application's <code>web.xml</code>.
 * <p>
 * By default, it looks for a {@link DeployedMigrator} as a bean named <code>beringMigrator</code>.
 * This name can be changed by setting the <code>beringMigratorBeanName</code> context
 * initialization parameter.
 * <p>
 * If initializing the singletons in your application context requires your database schema to be
 * up-to-date (e.g., a Hibernate SessionFactory), this listener will not work.
 *
 * @author Rhett Sutphin
 */
public class SpringBeringContextListener extends BeringContextListener {
    public static final String BEAN_NAME_INIT_PARAMETER_NAME = "beringMigratorBeanName";
    public static final String DEFAULT_MIGRATOR_BEAN_NAME = "beringMigrator";

    /**
     * Retrieves the migrator from the {@link org.springframework.web.context.WebApplicationContext}.
     */
    @Override
    protected DeployedMigrator getDeployedMigrator(ServletContext servletContext) {
        String beanName = getMigratorBeanName(servletContext);
        return (DeployedMigrator) getApplicationContext(servletContext).getBean(beanName);
    }

    protected ApplicationContext getApplicationContext(ServletContext servletContext) {
        return WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
    }

    private String getMigratorBeanName(ServletContext servletContext) {
        String beanName = servletContext.getInitParameter(BEAN_NAME_INIT_PARAMETER_NAME);
        return beanName == null ? DEFAULT_MIGRATOR_BEAN_NAME : beanName;
    }
}
