package edu.northwestern.bioinformatics.bering.dialect;

import static edu.northwestern.bioinformatics.bering.dialect.DialectFactory.*;
import edu.northwestern.bioinformatics.bering.BeringTestCase;
import edu.northwestern.bioinformatics.bering.BeringException;
import edu.northwestern.bioinformatics.bering.runtime.BeringTaskException;

/**
 * @author Rhett Sutphin
 */
public class DialectFactoryTest extends BeringTestCase {
    public void testBuildDialectByName() throws Exception {
        Dialect actual = buildDialect(Hsqldb.class.getName());
        assertNotNull(actual);
        assertTrue(actual instanceof Hsqldb);
    }

    public void testInvalidDialectName() throws Exception {
        try {
            buildDialect("this is not a class");
            fail("Exception not thrown");
        } catch (DialectException e) {
            assertEquals("Could not find dialect class this is not a class", e.getMessage());
        }
    }

    public void testNonDialectDialectName() throws Exception {
        try {
            buildDialect(String.class.getName());
            fail("Exception not thrown");
        } catch (DialectException e) {
            assertEquals("Class java.lang.String does not implement " + Dialect.class.getName(), e.getMessage());
        }
    }

    public void testGuessDialectForHSQLDB() throws Exception {
        Dialect guessed = guessDialect("HSQL Database Engine");
        assertNotNull(guessed);
        assertTrue(guessed instanceof Hsqldb);
    }

    public void testGuessDialectForPostgreSQL() throws Exception {
        Dialect guessed = guessDialect("PostgreSQL");
        assertNotNull(guessed);
        assertTrue(guessed instanceof PostgreSQL);
    }

    public void testGuessDialectForOracle() throws Exception {
        Dialect guessed = guessDialect("Oracle");
        assertNotNull(guessed);
        assertTrue(guessed instanceof Oracle);
    }

    public void testGuessUnknownDialect() throws Exception {
        try {
            guessDialect("SuperDB");
            fail("No exception");
        } catch (UnguessableDialectException ude) {
            assertEquals(ude.getMessage(), "Unable to pick a dialect for SuperDB.  Please specify one explicitly.");
        }
    }
}
