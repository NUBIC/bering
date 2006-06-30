package edu.northwestern.bioinformatics.bering;

import junit.framework.TestCase;

import java.io.File;
import java.net.URL;
import java.net.URISyntaxException;
import java.net.URI;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.LinkedList;

import org.easymock.classextension.EasyMock;

/**
 * @author rsutphin
 */
public abstract class BeringTestCase extends TestCase {
    private List<Object> mocks;

    private List<Object> getMocks() {
        if (mocks == null) { mocks = new LinkedList<Object>(); }
        return mocks;
    }

    protected <T> T registerMockFor(Class<T> toMock) {
        T mock = EasyMock.createMock(toMock);
        getMocks().add(mock);
        return mock;
    }

    protected void replayMocks() {
        for (Object o : getMocks()) EasyMock.replay(o);
    }

    protected void verifyMocks() {
        for (Object o : getMocks()) EasyMock.verify(o);
    }

    protected void resetMocks() {
        for (Object o : getMocks()) EasyMock.reset(o);
    }

    public static void assertPositive(String message, long value) {
        assertTrue(prependMessage(message) + value + " is not positive", value > 0);
    }

    public static void assertPositive(long value) {
        assertPositive(null, value);
    }

    public static void assertNonnegative(String message, long value) {
        assertTrue(prependMessage(message) + value + " is not nonnegative", value >= 0);
    }

    public static void assertNonnegative(long value) {
        assertNonnegative(null, value);
    }

    public static void assertNonpositive(String message, long value) {
        assertTrue(prependMessage(message) + value + " is not nonpositive", value <= 0);
    }

    public static void assertNonpositive(long value) {
        assertNonpositive(null, value);
    }

    public static void assertNegative(String message, long value) {
        assertTrue(prependMessage(message) + value + " is not negative", value < 0);
    }

    public static void assertNegative(long value) {
        assertNegative(null, value);
    }

    private static String prependMessage(String message) {
        return (message == null ? "" : message + ": ");
    }

    public static File getClassRelativeFile(Class<?> base, String filename) {
        StringBuffer full = new StringBuffer()
            .append('/')
            .append(base.getPackage().getName().replace('.', '/'))
            .append('/').append(filename);
        String resName = URI.create(full.toString()).normalize().toString();
        URL url = base.getResource(resName);
        if (url == null) {
            throw new IllegalArgumentException("Could not find resource: " + resName);
        }
        try {
            return new File(url.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException("Invalid file resource URL: " + url.toString(), e);
        }
    }
}
