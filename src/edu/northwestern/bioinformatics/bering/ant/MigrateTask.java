package edu.northwestern.bioinformatics.bering.ant;

import edu.northwestern.bioinformatics.bering.DatabaseAdapter;
import edu.northwestern.bioinformatics.bering.Main;
import edu.northwestern.bioinformatics.bering.dialect.Dialect;
import edu.northwestern.bioinformatics.bering.dialect.Generic;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.JDBCTask;

import java.beans.PropertyEditor;
import java.io.File;
import java.io.IOException;

/**
 * @author Moses Hohman
 */
public class MigrateTask extends JDBCTask {
    private String migrationsDir = "db/migrate";
    private String dialect = Generic.class.getName();
    private Integer targetMigration;
    private Integer targetRelease;

    public void execute() throws BuildException {
        Main command = new Main();
        command.setRootDir(createMigrationDirectory().getAbsolutePath());
        DatabaseAdapter adapter = createAdapter();
        command.setAdapter(adapter);
        command.migrate(getTargetRelease(), getTargetMigration());
        adapter.close();
    }

    private DatabaseAdapter createAdapter() {
        return new DatabaseAdapter(getConnection(), createDialect());
    }

    // package-level for testing
    Dialect createDialect() {
        String d = getDialect();
        try {
            return (Dialect) Class.forName(d).newInstance();
        } catch (InstantiationException e) {
            throw new BuildException("Could not create an instance of dialect " + d, e);
        } catch (IllegalAccessException e) {
            throw new BuildException("Could not create an instance of dialect " + d, e);
        } catch (ClassNotFoundException e) {
            throw new BuildException("Could not find dialect class " + d, e);
        } catch (ClassCastException e) {
            throw new BuildException("Class " + d + " does not implement " + Dialect.class.getName(), e);
        }
    }

    private File createMigrationDirectory() {
        File migrationDirectory = new File(getMigrationsDir());
        if (!migrationDirectory.isAbsolute()) {
            migrationDirectory = new File(getProject().getBaseDir(), getMigrationsDir());
        }
        if (!migrationDirectory.isDirectory()) {
            try {
                throw new BuildException(migrationDirectory.getCanonicalPath() + " is not a directory");
            } catch (IOException e) {
                throw new BuildException("bad migration directory name: " + migrationDirectory.toString(), e);
            }
        }
        return migrationDirectory;
    }

    ////// TASK CONFIGURATION

    public String getMigrationsDir() {
        return migrationsDir;
    }

    public void setMigrationsDir(String migrationsDir) {
        this.migrationsDir = migrationsDir;
    }

    public String getDialect() {
        return dialect;
    }

    public void setDialect(String dialect) {
        this.dialect = dialect;
    }

    public void setTargetVersion(String version) {
        PropertyEditor editor = new TargetVersionEditor();
        try {
            editor.setAsText(version);
        } catch (IllegalArgumentException e) {
            throw new BuildException("Invalid target version (" + version
                + ").  Should have the form 'R|M' or 'M'.", e);
        }
        Integer[] parsedVersion = (Integer[]) editor.getValue();
        targetRelease   = parsedVersion[0];
        targetMigration = parsedVersion[1];
    }

    public Integer getTargetMigration() {
        return targetMigration;
    }

    public Integer getTargetRelease() {
        return targetRelease;
    }
}
