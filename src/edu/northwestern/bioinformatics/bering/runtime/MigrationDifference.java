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
        this.currentMigration = currentMigration == null ? 0 : currentMigration;
        this.targetMigration = targetMigration == null ? release.getMaxScriptNumber() : targetMigration;
    }

    public boolean isUp() {
        return targetMigration >= currentMigration;
    }

    public List<Script> getScriptsToRun() {
        List<Integer> scriptNumbers;
        if (targetMigration == currentMigration) {
            scriptNumbers = Collections.emptyList();
        } else if (isUp()) {
            scriptNumbers = getUpNumbers();
        } else {
            scriptNumbers = getDownNumbers();
        }
        List<Script> scripts = new LinkedList<Script>();
        for (Integer n : scriptNumbers) {
            Script script = release.getScript(n);
            if (script != null) scripts.add(script);
        }
        return scripts;
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
