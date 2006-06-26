package edu.northwestern.bioinformatics.bering;

import java.util.LinkedList;
import java.util.Queue;

/**
 * @author Moses Hohman
 */
public class StubAdapter implements Adapter {
    private Queue<String> statements = new LinkedList<String>();

    public void execute(String sql) {
        statements.add(sql);
    }

    public Queue<String> getStatements() {
        return statements;
    }
}
