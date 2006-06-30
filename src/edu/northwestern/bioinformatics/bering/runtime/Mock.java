package edu.northwestern.bioinformatics.bering.runtime;

import java.io.File;
import java.util.Collection;

/**
 * @author rsutphin
 */
public class Mock {
    public static final class Script extends edu.northwestern.bioinformatics.bering.runtime.Script {
        private int number;
        private boolean upCalled;
        private boolean downCalled;

        public Script(Release release, Integer number) {
            super(null, release);
            this.number = number;
        }

        public String getName() {
            return "mock";
        }

        public Integer getNumber() {
            return number;
        }

        public boolean upCalled() {
            return upCalled;
        }

        public boolean downCalled() {
            return downCalled;
        }

        public void up() {
            upCalled = true;
        }

        public void down() {
            downCalled = true;
        }
    }

    public static final class Release extends edu.northwestern.bioinformatics.bering.runtime.Release {
        private int number;

        public Release(int number, int... scriptNumbers) {
            super(null);
            this.number = number;
            addScripts(scriptNumbers);
        }

        public Integer getNumber() {
            return number;
        }

        public void addScripts(int... scriptNumber) {
            for (int n : scriptNumber) {
                addScript(new Script(this, n));
            }
        }
    }

    public static final class Finder extends MigrationFinder {
        public Finder() {
            super(null);
        }

        public void addRelease(int number, int... scriptNumbers) {
            super.addRelease(new Release(number, scriptNumbers));
        }
    }
}
