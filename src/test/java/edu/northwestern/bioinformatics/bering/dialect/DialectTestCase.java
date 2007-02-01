package edu.northwestern.bioinformatics.bering.dialect;

import edu.northwestern.bioinformatics.bering.BeringTestCase;
import edu.northwestern.bioinformatics.bering.TableDefinition;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * @author Rhett Sutphin
 */
public abstract class DialectTestCase<D extends Dialect> extends BeringTestCase {
    private D dialect;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        dialect = createDialect();
    }

    private D createDialect() throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        return getDialectClass().getConstructor().newInstance();
    }

    protected abstract Class<D> getDialectClass();

    protected final D getDialect() { return dialect; }

    protected static void assertStatements(List<String> actual, String... expected) {
        assertEquals("Wrong number of statements: " + actual, expected.length, actual.size());
        for (int i = 0; i < expected.length; i++) {
            assertEquals("Wrong statment " + i, expected[i], actual.get(i));
        }
    }

    protected static TableDefinition createTestTable() {
        TableDefinition def = new TableDefinition("feast");
        def.addColumn("name", "string");
        def.addColumn("length", "integer");
        return def;
    }

    ////// SHAREABLE TESTS

}
