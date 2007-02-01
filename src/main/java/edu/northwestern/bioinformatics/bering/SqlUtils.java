package edu.northwestern.bioinformatics.bering;

/**
 * @author Rhett Sutphin
 */
public class SqlUtils {

    public static String sqlLiteral(String value) {
        return value == null ? "NULL" : '\'' + value + '\'';
    }

    public static String sqlLiteral(Object value) {
        if (value instanceof String) return sqlLiteral((String) value);
        return value == null ? "NULL" : value.toString();
    }
}
