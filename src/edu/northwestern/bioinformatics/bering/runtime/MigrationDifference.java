package edu.northwestern.bioinformatics.bering.runtime;

import edu.northwestern.bioinformatics.bering.runtime.Release;
import edu.northwestern.bioinformatics.bering.Migration;

import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Collections;

/**
 * @author rsutphin
 */
public class MigrationDifference {
    private Release release;
    private Integer targetMigration, currentMigration;

    public MigrationDifference(Release release, Integer currentMigration, Integer targetMigration) {
        this.release = release;
        this.targetMigration = targetMigration == null ? release.getScripts().size() : targetMigration;
        this.currentMigration = currentMigration == null ? 0 : currentMigration;
    }

    public boolean isUp() {
        return targetMigration >= currentMigration;
    }

    public List<Migration> getMigrationsToRun() {
        List<Integer> scriptNumbers;
        if (targetMigration == currentMigration) {
            scriptNumbers = Collections.emptyList();
        } else if (isUp()) {
            scriptNumbers = getUpNumbers();
        } else {
            scriptNumbers = getDownNumbers();
        }
        List<Migration> migrations = new LinkedList<Migration>();
        for (Integer n : scriptNumbers) {
            migrations.add(release.getScript(n).createMigrationInstance());
        }
        return migrations;
    }

    private List<Integer> getUpNumbers() {
        return createNumberList(
            currentMigration + 1,
            targetMigration);
    }

    private List<Integer> getDownNumbers() {
        return createNumberList(
            currentMigration,
            targetMigration + 1
        );
    }

    private List<Integer> createNumberList(int firstIndex, int lastIndex) {
        List<Integer> idxes = new ArrayList<Integer>();
        int start, stop;
        boolean descending = firstIndex > lastIndex;
        if (descending) {
            start = lastIndex;
            stop = firstIndex;
        } else {
            start = firstIndex;
            stop = lastIndex;
        }

        for (int i = start; i <= stop; i++) {
            idxes.add(i);
        }

        if (descending) {
            Collections.reverse(idxes);
        }

        return idxes;
    }
}
