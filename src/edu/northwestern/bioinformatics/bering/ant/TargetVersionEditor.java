package edu.northwestern.bioinformatics.bering.ant;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;

/**
 * This editor converts a string of the form <code>"M|N"</code> or <code>"N"</code> to a two-position
 * {@link Integer} array where <code>M</code> is in the zeroth position in the array.  If the
 * string is the empty string (<code>""</code>), it will return null in both positions.
 * Both <code>M</code> and <code>N</code> must be integers, if specified.
 *
 * @author Rhett Sutphin
 */
public class TargetVersionEditor extends PropertyEditorSupport {
    private Integer[] value = new Integer[2];

    public void setAsText(String text) throws IllegalArgumentException {
        int separatorIndex = findSeparator(text);
        if (text.length() == 0) {
            value[0] = null;
            value[1] = null;
        } else if (separatorIndex >= 0) {
            value[0] = new Integer(text.substring(0, separatorIndex));
            value[1] = new Integer(text.substring(separatorIndex + 1));
        } else {
            value[0] = null;
            value[1] = new Integer(text);
        }
    }

    private int findSeparator(String text) {
        return text.indexOf('|');
    }

    public Object getValue() {
        return value;
    }
}
