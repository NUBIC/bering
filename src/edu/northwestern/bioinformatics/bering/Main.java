package edu.northwestern.bioinformatics.bering;

import edu.northwestern.bioinformatics.bering.runtime.MigrationFinder;
import edu.northwestern.bioinformatics.bering.runtime.Version;
import edu.northwestern.bioinformatics.bering.runtime.Target;

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
     * <p>
     *
     * </p>
     *
     * @param targetReleaseNumber
     * @param targetMigrationNumber
     */
    public void migrate(Integer targetReleaseNumber, Integer targetMigrationNumber) {
        // load migration scripts
        MigrationFinder finder = new MigrationFinder(new File(rootDir));

        // validate release/migration combination
        Target target = Target.create(finder, targetReleaseNumber, targetMigrationNumber);

        // load version table
        Version current = adapter.loadVersions();

        // walk through scripts, running ones that haven't been run, up to / down to desired release/version
        if (target.getReleaseNumber() >= current.getReleaseNumbers().last()) {
            // TODO
        }
    }

    ////// CONFIGURATION

    public void setRootDir(String rootDir) {
        this.rootDir = rootDir;
    }

    public void setAdapter(Adapter adapter) {
        this.adapter = adapter;
    }

}
