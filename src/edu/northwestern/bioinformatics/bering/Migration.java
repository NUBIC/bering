package edu.northwestern.bioinformatics.bering;

import groovy.lang.Closure;

/**
 * @author Moses Hohman
 */
public class Migration {
    private Adapter adapter;

    protected void createTable(String name, Closure addContents) {
        TableDefinition definition = new TableDefinition(name, adapter);
        addContents.call(definition);
        adapter.createTable(definition);
    }

    protected void dropTable(String name) {
        adapter.dropTable(name);
    }

    public void setAdapter(Adapter adapter) {
        this.adapter = adapter;
    }
}
