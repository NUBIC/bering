package edu.northwestern.bioinformatics.bering;

import junit.framework.TestCase;

import java.io.File;
import java.net.URL;
import java.net.URISyntaxException;
import java.net.URI;

/**
 * @author rsutphin
 */
public abstract class BeringTestCase extends TestCase {
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
