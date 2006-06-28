class AddFrogs extends edu.northwestern.bioinformatics.bering.Migration {
    void up() {
        createTable('frogs') { t ->
            t.addColumn('name', 'string', nullable:false)
            t.addColumn('color', 'string', nullable:false)
            t.addColumn('comments', 'string')
        }
    }

    void down() {
        dropTable('frogs')
    }
}
