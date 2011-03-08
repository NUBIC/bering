package edu.northwestern.bioinformatics.bering.runtime;

import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author rsutphin
 */
public class Release extends MigrationElement {
    private SortedMap<Integer, Script> scripts;

    public Release(String elementName) {
        super(elementName);
        this.scripts = new TreeMap<Integer, Script>();
    }

    public void addScript(Script script) {
        Integer key = script.getNumber();
        if (scripts.containsKey(key)) {
            throw new IllegalStateException("More than one script in release " + getNumber() + " with number '" + key + '\'');
        }
        scripts.put(key, script);
    }

    public Collection<Script> getScripts() {
        return scripts.values();
    }

    public Script getScript(int number) {
        return scripts.get(number);
    }

    public Integer getMaxScriptNumber() {
        if (getScripts().size() == 0) return 0;
        return scripts.lastKey();
    }
}
