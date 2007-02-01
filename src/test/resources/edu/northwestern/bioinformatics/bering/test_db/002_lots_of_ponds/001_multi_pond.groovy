class MultiPond extends edu.northwestern.bioinformatics.bering.Migration {
    void up() {
        createTable('frogs_ponds') { t ->
            t.addColumn('frog_id', 'integer', nullable:false)
            t.addColumn('pond_id', 'integer')
        }

        // TODO: copy data from frogs.pond_id column into join table
        // TODO: foreign keys

        removeColumn('frogs', 'pond_id')
    }

    void down() {
        addColumn('frogs', 'pond_id', 'integer')

        // TODO: copy data back from join table into frogs table
        // TODO: recreate foreign key

        dropTable('frogs')
    }
}