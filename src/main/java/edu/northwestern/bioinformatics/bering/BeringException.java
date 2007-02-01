package edu.northwestern.bioinformatics.bering;

/**
 * @author Rhett Sutphin
 */
public abstract class BeringException extends RuntimeException {
    protected BeringException(String message) {
        super(message);
    }

    protected BeringException(String message, Throwable cause) {
        super(message, cause);
    }

    protected BeringException(Throwable cause) {
        super(cause);
    }
}
