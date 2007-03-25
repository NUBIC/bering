package edu.northwestern.bioinformatics.bering.servlet;

import edu.northwestern.bioinformatics.bering.BeringTestCase;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author Rhett Sutphin
 */
public class DeployedMigratorTest extends BeringTestCase {
    private DeployedMigrator migrator;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        migrator = new DeployedMigrator();
    }

    public void testJdbcTemplateDefaulted() throws Exception {
        SingleConnectionDataSource expectedDS = new SingleConnectionDataSource();
        migrator.setDataSource(expectedDS);
        JdbcTemplate actual = migrator.getJdbcTemplate();
        assertNotNull(actual);
        assertSame(expectedDS, actual.getDataSource());
    }
}
