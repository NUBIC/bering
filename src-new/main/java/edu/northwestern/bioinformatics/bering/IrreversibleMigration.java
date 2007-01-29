package edu.northwestern.bioinformatics.bering;

/**
 * @author rsutphin
 */
public class IrreversibleMigration extends RuntimeException {
    public IrreversibleMigration() {
        super();
    }

    public IrreversibleMigration(String message) {
         super(message);
    }

    public IrreversibleMigration(String message, Throwable cause) {
         super(message, cause);
    }
}
