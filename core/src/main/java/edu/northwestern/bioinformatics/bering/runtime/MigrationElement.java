package edu.northwestern.bioinformatics.bering.runtime;

/**
 * @author Rhett Sutphin
 */
public abstract class MigrationElement implements Comparable<MigrationElement> {
    private String elementName;
    private String name;
    private Integer number;

    public MigrationElement(String elementName) {
        this.elementName = elementName;
        // allow nulls for testing
        if (elementName != null) {
            int firstUnderscore = elementName.indexOf('_');
            if (firstUnderscore < 0) {
                initIndexAndName(elementName, null);
            } else {
                initIndexAndName(
                    elementName.substring(0, firstUnderscore),
                    elementName.substring(firstUnderscore + 1)
                );
            }
        }
    }

    protected void initIndexAndName(String indexString, String nameString) {
        name = nameString;
        number = new Integer(indexString);
    }

    public String getName() {
        return name;
    }

    public Integer getNumber() {
        return number;
    }

    public String getElementName() {
        return elementName;
    }

    public int compareTo(MigrationElement o) {
        return getNumber() - o.getNumber();
    }
}
