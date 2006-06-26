package edu.northwestern.bioinformatics.bering

import edu.northwestern.bioinformatics.bering.TableDefinition
import edu.northwestern.bioinformatics.bering.ColumnDefinition
import edu.northwestern.bioinformatics.bering.Migration

class AddFrogsMigration extends Migration {
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

new AddFrogsMigration().up()
new AddFrogsMigration().down()
