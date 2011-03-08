class AddPonds extends edu.northwestern.bioinformatics.bering.Migration {
    void up() {
        createTable('ponds') { t ->
            t.addColumn('name', 'string', nullable:false)
            t.addColumn('depth', 'integer')
        }

        // TODO: foreign keys
        addColumn('frogs', 'pond_id', 'integer')
    }

    void down() {
        removeColumn('frogs', 'pond_id')
        dropTable('frogs')
    }
}
