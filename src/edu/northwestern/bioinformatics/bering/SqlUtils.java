package edu.northwestern.bioinformatics.bering;

import org.apache.ddlutils.util.SqlTokenizer;

import java.util.List;
import java.util.LinkedList;

/**
 * @author Rhett Sutphin
 */
public class SqlUtils {

    public static String sqlLiteral(String value) {
        return value == null ? "NULL" : '\'' + value + '\'';
    }
}
