package edu.northwestern.bioinformatics.bering.runtime;

import edu.northwestern.bioinformatics.bering.DatabaseAdapter;
import edu.northwestern.bioinformatics.bering.Main;
import edu.northwestern.bioinformatics.bering.BeringException;
import edu.northwestern.bioinformatics.bering.MigrationExecutionException;
import edu.northwestern.bioinformatics.bering.runtime.filesystem.FilesystemMigrationFinder;
import edu.northwestern.bioinformatics.bering.dialect.Dialect;
import edu.northwestern.bioinformatics.bering.dialect.DialectFactory;

import java.beans.PropertyEditor;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Rhett Sutphin
 */
public class MigrateTaskHelper {
    private String migrationsDir;
    private String dialectName;

    private Integer targetMigration;
    private Integer targetRelease;

    private HelperCallbacks callbacks;

    public MigrateTaskHelper(HelperCallbacks callbacks) {
        this.callbacks = callbacks;
    }

    public void execute() {
        DatabaseAdapter adapter = null;
        try {
            Main command = new Main();
            command.setFinder(new FilesystemMigrationFinder(getRootDir()));
            adapter = createAdapter();
            command.setAdapter(adapter);
            command.migrate(getTargetRelease(), getTargetMigration());
        } catch (BeringException e) {
            // Pass through bering exceptions
            throw e;
        } catch (RuntimeException re) {
            // wrap and rethrow all others
            throw new MigrationExecutionException(re);
        } finally {
            if (adapter != null) adapter.close();
        }
    }

    private File getRootDir() {
        return new File(createMigrationDirectory().getAbsolutePath());
    }

    private DatabaseAdapter createAdapter() {
        return new DatabaseAdapter(callbacks.getConnection(), createDialect());
    }

    // package-level for testing
    Dialect createDialect() {
        String d = getDialectName();
        if (d == null) return null;
        return DialectFactory.buildDialect(d);
    }

    private File createMigrationDirectory() {
        File migrationDirectory = new File(getMigrationsDir());
        if (!migrationDirectory.isAbsolute()) {
            migrationDirectory = callbacks.resolve(migrationDirectory);
        }
        if (!migrationDirectory.isDirectory()) {
            try {
                throw new BeringTaskException(migrationDirectory.getCanonicalPath() + " is not a directory");
            } catch (IOException e) {
                throw new BeringTaskException("bad migration directory name: " + migrationDirectory.toString(), e);
            }
        }
        return migrationDirectory;
    }

    public String getMigrationsDir() {
        return migrationsDir;
    }

    public void setMigrationsDir(String migrationsDir) {
        this.migrationsDir = migrationsDir;
    }

    public String getDialectName() {
        return dialectName;
    }

    public void setDialectName(String dialect) {
        if (dialect == null || dialect.trim().length() == 0) return;
        this.dialectName = dialect;
    }

    public void setTargetVersion(String version) {
        PropertyEditor editor = new TargetVersionEditor();
        try {
            editor.setAsText(version);
        } catch (IllegalArgumentException e) {
            throw new BeringTaskException("Invalid target version (" + version
                + ").  Should have the form 'R|M', 'R-M', or 'M'.", e);
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

    public static interface HelperCallbacks {
        /** Return the connection that should be used during execution. */
        Connection getConnection();

        /** Resolve the given relative directory against the default base directory. */
        File resolve(File f);
    }
}
