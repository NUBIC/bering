package edu.northwestern.bioinformatics.bering.dialect;

import edu.northwestern.bioinformatics.bering.BeringException;

/**
 * @author Rhett Sutphin
 */
public class DialectException extends BeringException {
    public DialectException(String message) {
        super(message);
    }

    public DialectException(String message, Throwable cause) {
        super(message, cause);
    }
}
