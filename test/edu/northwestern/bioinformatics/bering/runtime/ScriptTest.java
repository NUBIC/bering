package edu.northwestern.bioinformatics.bering.runtime;

import edu.northwestern.bioinformatics.bering.BeringTestCase;
import edu.northwestern.bioinformatics.bering.Migration;

import java.io.File;

/**
 * @author rsutphin
 */
public class ScriptTest extends BeringTestCase {
    private Script existingScript;

    protected void setUp() throws Exception {
        super.setUp();
        existingScript = new Script(
            getClassRelativeFile(getClass(),
                "../test_db/001_out_the_door/001_add_frogs.groovy"), null);
    }

    public void testNameAndIndexWithName() throws Exception {
        assertEquals(1, (int) existingScript.getNumber());
        assertEquals("add_frogs", existingScript.getName());
    }

    public void testNameAndIndexWithoutName() throws Exception {
        String filename = "../test_db/001/003.groovy";
        try {
            createScript(filename);
            fail("Exception not thrown");
        } catch (Exception e) {
            assertEquals("A name is required for scripts: " + filename, e.getMessage());
        }
    }

    public void testClassName() throws Exception {
        assertEquals("AddFrogs", existingScript.getClassName());
    }

    public void testNaturalOrder() throws Exception {
        Script s1 = createScript("001_german.groovy");
        Script s2 = createScript("002_english.groovy");
        Script s3 = createScript("003_french.groovy");
        assertNegative(s1.compareTo(s2));
        assertNegative(s1.compareTo(s3));
        assertPositive(s2.compareTo(s1));
        assertNegative(s2.compareTo(s3));
        assertPositive(s3.compareTo(s1));
        assertPositive(s3.compareTo(s2));
    }

    public void testLoadClass() throws Exception {
        Class<? extends Migration> loaded = existingScript.loadClass();
        assertNotNull(loaded);
        assertEquals("AddFrogs", loaded.getSimpleName());
        assertTrue(Migration.class.isAssignableFrom(loaded));
    }

    public void testGetMigrationInstance() throws Exception {
        Migration migration = existingScript.createMigrationInstance();
        assertNotNull(migration);
    }

    private Script createScript(String filename) {
        return new Script(new File(filename), null);
    }
}
