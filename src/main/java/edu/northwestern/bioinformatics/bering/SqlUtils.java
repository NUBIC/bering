package edu.northwestern.bioinformatics.bering;

import org.apache.commons.lang.StringEscapeUtils;

/**
 * @author Rhett Sutphin
 */
public class SqlUtils {
    public static String sqlLiteral(String value) {
        return value == null ? "NULL" : String.format("'%s'", StringEscapeUtils.escapeSql(value));
    }

    public static String sqlLiteral(Object value) {
        if (value instanceof String) return sqlLiteral((String) value);
        return value == null ? "NULL" : value.toString();
    }
}
