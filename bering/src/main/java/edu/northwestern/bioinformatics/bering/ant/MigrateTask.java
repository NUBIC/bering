package edu.northwestern.bioinformatics.bering.ant;

import edu.northwestern.bioinformatics.bering.runtime.MigrateTaskHelper;
import edu.northwestern.bioinformatics.bering.runtime.BeringTaskException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.JDBCTask;

import java.sql.Connection;
import java.io.File;

/**
 * @author Moses Hohman
 */
public class MigrateTask extends JDBCTask {
    private MigrateTaskHelper helper;

    public MigrateTask() {
        helper = new MigrateTaskHelper(createHelperCallbacks());
        setMigrationsDir("db/migrate");
    }

    // package-level for testing
    TaskHelperCallbacks createHelperCallbacks() {
        return new TaskHelperCallbacks();
    }

    @Override
    public void execute() throws BuildException {
        try {
            getHelper().execute();
        } catch (BeringTaskException bte) {
            throw new BuildException(bte.getMessage(), bte);
        }
    }

    private MigrateTaskHelper getHelper() {
        return helper;
    }

    ////// TASK CONFIGURATION

    public String getMigrationsDir() {
        return getHelper().getMigrationsDir();
    }

    public void setMigrationsDir(String migrationsDir) {
        getHelper().setMigrationsDir(migrationsDir);
    }

    public String getDialect() {
        return getHelper().getDialectName();
    }

    public void setDialect(String dialect) {
        getHelper().setDialectName(dialect);
    }

    public void setTargetVersion(String version) {
        try {
            getHelper().setTargetVersion(version);
        } catch (BeringTaskException bte) {
            throw new BuildException(bte.getMessage(), bte);
        }
    }

    class TaskHelperCallbacks implements MigrateTaskHelper.HelperCallbacks {
        public Connection getConnection() {
            return MigrateTask.this.getConnection();
        }

        public File resolve(File f) {
            return new File(getProject().getBaseDir(), f.getPath());
        }
    }
}
