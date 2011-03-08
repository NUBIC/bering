package edu.northwestern.bioinformatics.bering;

/**
 * @author rsutphin
 */
public class StubMigration extends Migration {
    @Override
    public void up() { }

    @Override
    public void down() throws IrreversibleMigration { }

    public Adapter getAdapter() {
        return adapter;
    }
}
