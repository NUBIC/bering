package edu.northwestern.bioinformatics.bering.runtime;

import edu.northwestern.bioinformatics.bering.Adapter;
import edu.northwestern.bioinformatics.bering.BeringTestCase;
import edu.northwestern.bioinformatics.bering.IrreversibleMigration;
import edu.northwestern.bioinformatics.bering.Migration;
import org.easymock.EasyMock;

import java.io.File;

/**
 * @author rsutphin
 */
public class ScriptTest extends BeringTestCase {
    private static final String VALID_TEXT
        = "class AddFrogs extends edu.northwestern.bioinformatics.bering.Migration {\nvoid up() { }\nvoid down() { }\n}";
    private Script validScript;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        validScript = new Script("001_add_frogs", VALID_TEXT, null);
    }

    public void testNameAndIndexWithName() throws Exception {
        assertEquals(1, (int) validScript.getNumber());
        assertEquals("add_frogs", validScript.getName());
    }

    public void testNameAndIndexWithoutName() throws Exception {
        try {
            new Script("003", "", null);
            fail("Exception not thrown");
        } catch (Exception e) {
            assertEquals("A name is required for scripts: 003", e.getMessage());
        }
    }

    public void testClassName() throws Exception {
        assertEquals("AddFrogs", validScript.getClassName());
    }

    public void testNaturalOrder() throws Exception {
        Script s1 = createScript("001_german");
        Script s2 = createScript("002_english");
        Script s3 = createScript("003_french");
        assertNegative(s1.compareTo(s2));
        assertNegative(s1.compareTo(s3));
        assertPositive(s2.compareTo(s1));
        assertNegative(s2.compareTo(s3));
        assertPositive(s3.compareTo(s1));
        assertPositive(s3.compareTo(s2));
    }

    public void testLoadClass() throws Exception {
        Class<? extends Migration> loaded = validScript.loadClass();
        assertNotNull(loaded);
        assertEquals("AddFrogs", loaded.getSimpleName());
        assertTrue(Migration.class.isAssignableFrom(loaded));
    }

    public void testCreateMigrationInstance() throws Exception {
        Migration migration = validScript.createMigrationInstance(null);
        assertNotNull(migration);
    }

    public void testUpSetsAdapterOnMigration() {
        StubScript script = new StubScript();
        Adapter adapter = EasyMock.createMock(Adapter.class);
        script.up(adapter);
        assertEquals(adapter, script.getSingletonMigration().getAdapter());
    }

    public void testDownSetsAdapterOnMigration() {
        StubScript script = new StubScript();
        Adapter adapter = EasyMock.createMock(Adapter.class);
        script.down(adapter);
        assertEquals(adapter, script.getSingletonMigration().getAdapter());
    }

    private static class StubScript extends Script {
        private StubMigration migration = new StubMigration();

        public StubScript() {
            super("001_stub_script", "", null);
        }

        public StubMigration getSingletonMigration() {
            return migration;
        }

        @Override
        public Migration createMigrationInstance(Adapter adapter) {
            migration.setAdapter(adapter);
            return migration;
        }

        private static class StubMigration extends Migration {
            public Adapter getAdapter() {
                return adapter;
            }

            @Override public void up() { }

            @Override public void down() throws IrreversibleMigration { }
        }
    }

    private Script createScript(String elementName) {
        return new Script(elementName, null, null);
    }
}
