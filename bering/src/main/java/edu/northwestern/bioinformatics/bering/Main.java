package edu.northwestern.bioinformatics.bering;

import edu.northwestern.bioinformatics.bering.runtime.filesystem.FilesystemMigrationFinder;
import edu.northwestern.bioinformatics.bering.runtime.Version;
import edu.northwestern.bioinformatics.bering.runtime.Target;
import edu.northwestern.bioinformatics.bering.runtime.Migrator;
import edu.northwestern.bioinformatics.bering.runtime.MigrationFinder;

import java.io.File;

/**
 * @author rsutphin
 */
public class Main {
    private String rootDir;
    private Adapter adapter;

    /**
     * Migrate to the given release and migration, moving up or down as necessary.
     * If <code>targetMigrationNumber</code> is null, the database will be migrated to the
     * last script in <code>targetReleaseNumber</code>.  If <code>targetReleaseNumber</code>
     * is null, the target will be the latest release.
     *
     * @param targetReleaseNumber
     * @param targetMigrationNumber
     */
    public void migrate(Integer targetReleaseNumber, Integer targetMigrationNumber) {
        // load migration scripts
        MigrationFinder finder = new FilesystemMigrationFinder(new File(rootDir));

        // validate release/migration combination
        Target target = Target.create(finder, targetReleaseNumber, targetMigrationNumber);

        // load version table
        Version current = adapter.loadVersions();

        // walk through scripts, running ones that haven't been run, up to / down to desired release/version
        createMigrator(finder, current, target).migrate();
    }

    protected Migrator createMigrator(MigrationFinder finder, Version current, Target target) {
        return new Migrator(adapter, finder, current, target);
    }

    ////// CONFIGURATION

    public void setRootDir(String rootDir) {
        this.rootDir = rootDir;
    }

    public void setAdapter(Adapter adapter) {
        this.adapter = adapter;
    }
}
