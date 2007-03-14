package edu.northwestern.bioinformatics.bering.dialect;

/**
 * @author Rhett Sutphin
 */
public class UnguessableDialectException extends DialectException {
    public UnguessableDialectException(String productName) {
        super("Unable to pick a dialect for " + productName + ".  Please specify one explicitly.");
    }
}
