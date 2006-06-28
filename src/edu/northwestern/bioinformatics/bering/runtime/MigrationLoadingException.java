package edu.northwestern.bioinformatics.bering.runtime;

/**
 * @author rsutphin
 */
public class MigrationLoadingException extends RuntimeException {
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
