package edu.northwestern.bioinformatics.bering.dialect;

import org.apache.ddlutils.Platform;
import org.apache.ddlutils.util.SqlTokenizer;
import org.apache.ddlutils.model.Table;

import java.util.List;
import java.util.LinkedList;

/**
 * @author Rhett Sutphin
 */
public abstract class DdlUtilsBasedDialect implements Dialect {
    private Platform platform;

    public String getDialectName() {
        return getPlatform().getName();
    }

    public Platform getPlatform() {
        return platform;
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
    }
}
