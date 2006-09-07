package edu.northwestern.bioinformatics.bering.dialect;

import edu.northwestern.bioinformatics.bering.BeringTestCase;
import org.apache.ddlutils.Platform;

import java.util.List;

/**
 * @author Rhett Sutphin
 */
public abstract class DdlUtilsDialectTestCase<D extends DdlUtilsBasedDialect> extends BeringTestCase {
    private D dialect;
    private Platform platform;

    protected void setUp() throws Exception {
        super.setUp();
        dialect = createDialect();
        platform = registerMockFor(Platform.class);
        dialect.setPlatform(platform);
    }

    protected abstract D createDialect();

    protected final D getDialect() { return dialect; }
    protected final Platform getPlatform() { return platform; }

    protected static void assertStatements(List<String> actual, String... expected) {
        assertEquals("Wrong number of statements: " + actual, expected.length, actual.size());
        for (int i = 0; i < expected.length; i++) {
            assertEquals("Wrong statment " + i, expected[0], actual.get(0));
        }
    }
}
