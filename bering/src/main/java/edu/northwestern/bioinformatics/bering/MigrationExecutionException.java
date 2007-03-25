package edu.northwestern.bioinformatics.bering;

/**
 * @author Rhett Sutphin
 */
public class MigrationExecutionException extends BeringException {
    public MigrationExecutionException(String message) {
        super(message);
    }

    public MigrationExecutionException(String message, Throwable cause) {
        super(message, cause);
    }

    public MigrationExecutionException(Throwable cause) {
        super(cause);
    }
}
