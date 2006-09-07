package edu.northwestern.bioinformatics.bering;

import org.apache.ddlutils.util.SqlTokenizer;

import java.util.List;
import java.util.LinkedList;

/**
 * @author Rhett Sutphin
 */
public class SqlUtils {

    public static List<String> separateStatements(String multistatmentSql) {
        SqlTokenizer tok = new SqlTokenizer(multistatmentSql);
        List<String> stmts = new LinkedList<String>();
        while (tok.hasMoreStatements()) {
            stmts.add(tok.getNextStatement());
        }
        return stmts;
    }

    public static String sqlLiteral(String value) {
        return value == null ? "NULL" : '\'' + value + '\'';
    }
}
