package edu.northwestern.bioinformatics.bering;

import groovy.lang.Closure;

/**
 * @author Moses Hohman
 */
public class Migration {
    private Adapter adapter = new MockAdapter();

    protected void createTable(String name, Closure addContents) {
        TableDefinition definition = new TableDefinition(name);
        addContents.call(definition);
        adapter.execute(definition.toSql());
    }

    protected void dropTable(String name) {
        adapter.execute("DROP TABLE " + name);
    }

    private class MockAdapter implements Adapter {
        public void execute(String sql) {
            System.out.println(sql);
        }
    }
}
