package edu.northwestern.bioinformatics.bering.dialect;

import edu.northwestern.bioinformatics.bering.BeringTestCase;
import org.apache.ddlutils.Platform;
import org.apache.ddlutils.PlatformInfo;
import org.easymock.classextension.EasyMock;

import java.util.List;

/**
 * @author Rhett Sutphin
 */
public abstract class DdlUtilsDialectTestCase<D extends DdlUtilsBasedDialect> extends BeringTestCase {
    private D dialect;
    private Platform platform;
    private PlatformInfo platformInfo;

    protected void setUp() throws Exception {
        super.setUp();
        dialect = createDialect();
        platform = registerMockFor(Platform.class);
        platformInfo = registerMockFor(PlatformInfo.class);
        dialect.setPlatform(platform);
        EasyMock.expect(platform.getPlatformInfo()).andReturn(platformInfo).anyTimes();
    }

    protected abstract D createDialect();

    protected final D getDialect() { return dialect; }
    protected final Platform getPlatform() { return platform; }
    public final PlatformInfo getPlatformInfo() { return platformInfo; }

    protected static void assertStatements(List<String> actual, String... expected) {
        assertEquals("Wrong number of statements: " + actual, expected.length, actual.size());
        for (int i = 0; i < expected.length; i++) {
            assertEquals("Wrong statment " + i, expected[i], actual.get(i));
        }
    }
}
