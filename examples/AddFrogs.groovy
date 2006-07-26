package edu.northwestern.bioinformatics.bering

import edu.northwestern.bioinformatics.bering.Migration

class AddFrogsMigration extends Migration {
    void up() {
        createTable('frogs') { t ->
            t.addColumn('name', 'string', nullable:false)
            t.addColumn('color', 'string', nullable:false)
            t.addColumn('comments', 'string')
        }

        // TODO: implement execute
        if (databaseMatches('oracle')) {
            execute("COMMENT ON TABLE frogs IS 'This is the frogs table in an Oracle DB'")
        } else if (databaseMatches('postgresql') {
            execute("COMMENT ON TABLE frogs IS 'This is the frogs table in a PostgreSQL DB'")
        } else {
            execute("COMMENT ON TABLE frogs IS 'This is the frogs table in some other DB'")
        }
    }

    void down() {
        dropTable('frogs')
    }
}

new AddFrogsMigration().up()
new AddFrogsMigration().down()
