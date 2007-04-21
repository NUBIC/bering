package edu.northwestern.bioinformatics.bering.runtime;

import edu.northwestern.bioinformatics.bering.Adapter;
import edu.northwestern.bioinformatics.bering.IrreversibleMigration;

/**
 * @author Rhett Sutphin
 */
public class Mock {
    public static final class Script extends edu.northwestern.bioinformatics.bering.runtime.Script {
        private int number;
        private boolean upCalled;
        private boolean downCalled;
        private RuntimeException runException;

        public Script(Release release, Integer number) {
            super(null, null, release);
            this.number = number;
        }

        public void setRunException(RuntimeException runException) {
            this.runException = runException;
        }

        @Override
        public Class<? extends Migration> loadClass() {
            return Mock.Script.Migration.class;
        }

        @Override
        public edu.northwestern.bioinformatics.bering.Migration createMigrationInstance(Adapter adapter) {
            return new Mock.Script.Migration();
        }

        @Override
        public String getName() {
            return "mock";
        }

        @Override
        public Integer getNumber() {
            return number;
        }

        public boolean upCalled() {
            return upCalled;
        }

        public boolean downCalled() {
            return downCalled;
        }

        private class Migration extends edu.northwestern.bioinformatics.bering.Migration {
            @Override
            public void up() {
                if (runException != null) throw runException;
                upCalled = true;
            }

            @Override
            public void down() throws IrreversibleMigration {
                if (runException != null) throw runException;
                downCalled = true;
            }
        }
    }

    public static final class Release extends edu.northwestern.bioinformatics.bering.runtime.Release {
        private int number;

        public Release(int number, int... scriptNumbers) {
            super(null);
            this.number = number;
            addScripts(scriptNumbers);
        }

        @Override
        public Integer getNumber() {
            return number;
        }

        private void addScripts(int[] scriptNumber) {
            for (int n : scriptNumber) {
                addScript(new Script(this, n));
            }
        }
    }

    public static final class Finder extends AbstractMigrationFinder {
        public void addRelease(int number, int... scriptNumbers) {
            super.addRelease(new Release(number, scriptNumbers));
        }
    }

    private Mock() { }
}
