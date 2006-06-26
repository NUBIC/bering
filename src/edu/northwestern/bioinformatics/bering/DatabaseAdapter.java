package edu.northwestern.bioinformatics.bering;

import org.apache.ddlutils.Platform;
import org.apache.ddlutils.PlatformFactory;
import org.apache.ddlutils.model.Column;
import org.apache.ddlutils.model.Database;
import org.apache.ddlutils.model.Table;

import javax.sql.DataSource;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

/**
 * @author rsutphin
 */
public class DatabaseAdapter implements Adapter {
    private static final Map<String, Integer> namesToJdbcTypes = new HashMap<String, Integer>();
    static {
        namesToJdbcTypes.put("string",    Types.VARCHAR);
        namesToJdbcTypes.put("integer",   Types.INTEGER);
        namesToJdbcTypes.put("float",     Types.NUMERIC);
        namesToJdbcTypes.put("boolean",   Types.BOOLEAN);
        namesToJdbcTypes.put("date",      Types.DATE);
        namesToJdbcTypes.put("time",      Types.TIME);
        namesToJdbcTypes.put("timestamp", Types.TIMESTAMP);
    }

    private Platform platform;

    public DatabaseAdapter(DataSource dataSource) {
        this.platform = PlatformFactory.createNewPlatformInstance(dataSource);
    }

    public void createTable(TableDefinition def) {
        Database db = new Database();
        db.addTable(def.toTable());
        platform.createTables(db, false, false);
    }

    public void dropTable(String name) {
        Database db = new Database();
        Table toDrop = new Table();
        toDrop.setName(name);
        platform.dropTables(db, false);
    }

    public Column createPrimaryKeyColumn(String name) {
        Column col = new Column();
        col.setPrimaryKey(true);
        col.setName("id");
        col.setTypeCode(Types.INTEGER);
        return col;
    }

    public int getTypeCode(String typeName) {
        Integer code = namesToJdbcTypes.get(typeName);
        if (code == null) {
            throw new IllegalArgumentException("Unknown type: " + typeName);
        }
        return code;

    }
}
