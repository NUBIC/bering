package edu.northwestern.bioinformatics.bering;

import javax.sql.DataSource;

/**
 * @author Rhett Sutphin
 */
public interface DataSourceProvider {
    DataSource getDataSource();
}
