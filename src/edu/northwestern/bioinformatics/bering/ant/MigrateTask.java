package edu.northwestern.bioinformatics.bering.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.JDBCTask;
import edu.northwestern.bioinformatics.bering.Main;
import edu.northwestern.bioinformatics.bering.DatabaseAdapter;

import java.io.File;
import java.io.IOException;

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
        command.setAdapter(new DatabaseAdapter(getConnection()));
        command.migrate(getTargetRelease(), getTargetMigration());
    }

    public String getMigrationsDir() {
        return migrationsDir;
    }

    public void setMigrationsDir(String migrationsDir) {
        this.migrationsDir = migrationsDir;
    }

    public Integer getTargetMigration() {
        return targetMigration;
    }

    public void setTargetMigration(Integer targetMigration) {
        this.targetMigration = targetMigration;
    }

    public Integer getTargetRelease() {
        return targetRelease;
    }

    public void setTargetRelease(Integer targetRelease) {
        this.targetRelease = targetRelease;
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
