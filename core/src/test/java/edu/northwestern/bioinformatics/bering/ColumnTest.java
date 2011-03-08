package edu.northwestern.bioinformatics.bering;

import static edu.northwestern.bioinformatics.bering.Column.createColumn;
import static edu.northwestern.bioinformatics.bering.Migration.*;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Rhett Sutphin
 */
public class ColumnTest extends BeringTestCase {
    private Map<String, Object> parameters = new HashMap<String, Object>();

    public void testCreateColumn() throws Exception {
        Column actual = createColumn(null, "title", "string");
        assertEquals("Wrong name", "title", actual.getName());
        assertEquals("Wrong type", Types.VARCHAR, actual.getTypeCode());
        assertTrue("Should be nullable by default", actual.isNullable());
        assertTrue("Should have no size by default", actual.getTypeQualifiers().isEmpty());
    }

    public void testCreateNotNullColumn() throws Exception {
        parameters.put(NULLABLE_KEY, false);
        Column actual = createColumn(parameters, "notnull", "string");
        assertFalse("required not set correctly", actual.isNullable());
    }

    public void testCreateColumnWithIntegerDefaultValue() {
        parameters.put("defaultValue", 0);
        Column actual = createColumn(parameters, "position", "integer");
        assertEquals("0", actual.getDefaultValue());
    }

    public void testCreateColumnWithStringDefaultValue() {
        parameters.put("defaultValue", "days");
        Column actual = createColumn(parameters, "duration_unit", "string");
        assertEquals("days", actual.getDefaultValue());
    }

    public void testCreateColumnWithNullDefaultValueDoesntDoAnything() {
        parameters.put("defaultValue", null);
        Column actual = createColumn(parameters, "duration_unit", "string");
        assertNull("default value not null", actual.getDefaultValue());
    }

    public void testCreateColumnWithLimit() throws Exception {
        parameters.put(LIMIT_KEY, 255);
        Column actual = createColumn(parameters, "notnull", "string");
        assertEquals(255, (int) actual.getTypeQualifiers().getLimit());
    }

    public void testCreateColumnWithPrecision() throws Exception {
        parameters.put(PRECISION_KEY, 9);
        Column actual = createColumn(parameters, "notnull", "string");
        assertEquals(9, (int) actual.getTypeQualifiers().getPrecision());
    }

    public void testCreateColumnWithScale() throws Exception {
        parameters.put(SCALE_KEY, 3);
        Column actual = createColumn(parameters, "notnull", "string");
        assertEquals(3, (int) actual.getTypeQualifiers().getScale());
    }

    public void testCreateManualPrimaryKeyColumn() throws Exception {
        parameters.put(PRIMARY_KEY_KEY, true);
        Column actual = createColumn(parameters, "id", "integer");
        assertTrue(actual.isPrimaryKey());
    }

    public void testCreateColumnDefaultsToNotPk() throws Exception {
        parameters.put("someParam", "someValue");
        Column actual = createColumn(parameters, "id", "integer");
        assertFalse(actual.isPrimaryKey());
    }

    public void testCreateColumnWithReference() throws Exception {
        parameters.put(REFERENCES_KEY, "rooms");
        Column actual = createColumn(parameters, "room_id", "integer");
        assertEquals("rooms", actual.getTableReference());
    }

    public void testCreateColumnWithReferenceAndExplicitConstraintName() throws Exception {
        parameters.put(REFERENCES_KEY, "rooms");
        parameters.put(REFERENCE_NAME_KEY, "fk_etc");
        Column actual = createColumn(parameters, "room_id", "integer");
        assertEquals("fk_etc", actual.getTableReferenceName());
    }
}
