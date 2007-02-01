package edu.northwestern.bioinformatics.bering.runtime;

import edu.northwestern.bioinformatics.bering.BeringException;

/**
 * @author rsutphin
 */
public class MigrationLoadingException extends BeringException {
    public MigrationLoadingException(String message) {
        super(message);
    }

    public MigrationLoadingException(String message, Throwable cause) {
        super(message, cause);
    }

    public MigrationLoadingException(Throwable cause) {
        super(cause);
    }
}
