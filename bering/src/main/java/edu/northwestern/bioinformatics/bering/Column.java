package edu.northwestern.bioinformatics.bering;

import static edu.northwestern.bioinformatics.bering.Migration.*;

import java.util.Map;
import java.sql.Types;

/**
 * @author Rhett Sutphin
 */
public class Column {
    private String name;
    private int typeCode;

    private TypeQualifiers typeQualifiers = new TypeQualifiers();

    private boolean nullable = true;
    private Object defaultValue = null;
    private boolean primaryKey = false;

    public static final Column AUTOMATIC_PK = new Column();
    public static final Column VERSION_COLUMN = new Column();
    static {
        AUTOMATIC_PK.setName("id");
        AUTOMATIC_PK.setTypeCode(Types.INTEGER);
        AUTOMATIC_PK.setNullable(false);
        AUTOMATIC_PK.setPrimaryKey(true);

        VERSION_COLUMN.setName("version");
        VERSION_COLUMN.setTypeCode(Types.INTEGER);
        VERSION_COLUMN.setDefaultValue("0");
        VERSION_COLUMN.setNullable(false);
    }

    public static Column createColumn(Map<String, Object> parameters, String columnName, String columnType) {
        Column column = new Column();
        column.setName(columnName);
        column.setTypeCode(getTypeCode(columnType));
        if (parameters != null && !parameters.isEmpty()) {
            if (parameters.containsKey(NULLABLE_KEY)) {
                column.setNullable(((Boolean) parameters.get(NULLABLE_KEY)));
            }
            if (parameters.containsKey(DEFAULT_VALUE_KEY) && parameters.get(DEFAULT_VALUE_KEY) != null) {
                column.setDefaultValue(String.valueOf(parameters.get(DEFAULT_VALUE_KEY)));
            }
            if (parameters.containsKey(LIMIT_KEY)) {
                column.getTypeQualifiers().setLimit((Integer) parameters.get(LIMIT_KEY));
            }
            if (parameters.containsKey(PRECISION_KEY)) {
                column.getTypeQualifiers().setPrecision((Integer) parameters.get(PRECISION_KEY));
            }
            if (parameters.containsKey(SCALE_KEY)) {
                column.getTypeQualifiers().setScale((Integer) parameters.get(SCALE_KEY));
            }
            column.setPrimaryKey(hasPrimaryKey(parameters, false));
        }
        return column;
    }

    private static int getTypeCode(String typeName) {
        Integer code = NAMES_TO_JDBC_TYPES.get(typeName);
        if (code == null) {
            throw new IllegalArgumentException("Unknown type: " + typeName);
        }
        return code;
    }

    ////// BEAN PROPERTIES

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(int typeCode) {
        this.typeCode = typeCode;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
    }

    public TypeQualifiers getTypeQualifiers() {
        return typeQualifiers;
    }

    public boolean isNullable() {
        return nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }
}
