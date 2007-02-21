package edu.northwestern.bioinformatics.bering.runtime;

import edu.northwestern.bioinformatics.bering.BeringException;

/**
 * @author Rhett Sutphin
 */
public class BeringTaskException extends BeringException {
    public BeringTaskException(String message) {
        super(message);
    }

    public BeringTaskException(String message, Throwable cause) {
        super(message, cause);
    }

    public BeringTaskException(Throwable cause) {
        super(cause);
    }
}
