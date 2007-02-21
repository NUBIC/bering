package edu.northwestern.bioinformatics.bering.runtime;

import junit.framework.TestCase;

/**
 * @author Rhett Sutphin
 */
public class TargetVersionEditorTest extends TestCase {
    private TargetVersionEditor editor;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        editor = new TargetVersionEditor();
    }

    public void testSetAsTextTwoValues() throws Exception {
        editor.setAsText("4|7");
        Integer[] value = assertValidValue();
        assertEquals(4, (int) value[0]);
        assertEquals(7, (int) value[1]);
    }

    public void testSetAsTextTwoValuesWithDash() throws Exception {
        editor.setAsText("4-7");
        Integer[] value = assertValidValue();
        assertEquals(4, (int) value[0]);
        assertEquals(7, (int) value[1]);
    }

    public void testSetAsTextOneValue() throws Exception {
        editor.setAsText("9");
        Integer[] value = assertValidValue();
        assertNull("0 not null", value[0]);
        assertEquals(9, (int) value[1]);
    }

    public void testSetAsTextNoValues() throws Exception {
        editor.setAsText("");
        Integer[] value = assertValidValue();
        assertNull("0 not null", value[0]);
        assertNull("1 not null", value[1]);
    }

    private Integer[] assertValidValue() {
        Object valueObj = editor.getValue();
        assertNotNull("Value not returned", valueObj);
        assertTrue("Value not Integer[]", valueObj instanceof Integer[]);
        Integer[] value = (Integer[]) valueObj;
        assertEquals("Wrong number of array entries", 2, value.length);
        return value;
    }
}
