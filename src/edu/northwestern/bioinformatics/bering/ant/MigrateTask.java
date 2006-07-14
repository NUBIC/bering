package edu.northwestern.bioinformatics.bering.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.JDBCTask;
import edu.northwestern.bioinformatics.bering.Main;
import edu.northwestern.bioinformatics.bering.DatabaseAdapter;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.beans.PropertyEditor;

/**
 * @author Moses Hohman
 */
public class MigrateTask extends JDBCTask {
    private String migrationsDir = "db/migrate";
    private Integer targetMigration;
    private Integer targetRelease;

    public void execute() throws BuildException {
        Main command = new Main();
        command.setRootDir(createMigrationDirectory().getAbsolutePath());
        DatabaseAdapter adapter = new DatabaseAdapter(getConnection());
        command.setAdapter(adapter);
        command.migrate(getTargetRelease(), getTargetMigration());
        adapter.close();
    }

    public String getMigrationsDir() {
        return migrationsDir;
    }

    public void setMigrationsDir(String migrationsDir) {
        this.migrationsDir = migrationsDir;
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
}
