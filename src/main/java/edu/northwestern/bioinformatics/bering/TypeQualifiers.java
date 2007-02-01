package edu.northwestern.bioinformatics.bering;

/**
 * @author Rhett Sutphin
 */
public class TypeQualifiers implements Cloneable {
    private Integer limit;
    private Integer precision;
    private Integer scale;

    public TypeQualifiers() {
        this(null, null, null);
    }

    public TypeQualifiers(Integer limit, Integer precision, Integer scale) {
        this.limit = limit;
        this.precision = precision;
        this.scale = scale;
    }

    ////// LOGIC

    /**
     * @return a new {@link edu.northwestern.bioinformatics.bering.TypeQualifiers} instance
     *  which merges in the values in <code>other</code> whenever this object has no value
     */
    public TypeQualifiers merge(TypeQualifiers other) {
        TypeQualifiers merged = this.clone();
        if (!hasLimit()) {
            merged.setLimit(other.getLimit());
        }
        if (!hasScale()) {
            merged.setScale(other.getScale());
        }
        if (!hasPrecision()) {
            merged.setPrecision(other.getPrecision());
        }
        return merged;
    }

    public boolean isEmpty() {
        return !(hasLimit() || hasScale() || hasPrecision());
    }

    public boolean hasLimit() {
        return limit != null;
    }

    public boolean hasScale() {
        return scale != null;
    }

    public boolean hasPrecision() {
        return precision != null;
    }

    ////// BEAN PROPERTIES

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getPrecision() {
        return precision;
    }

    public void setPrecision(Integer precision) {
        this.precision = precision;
    }

    public Integer getScale() {
        return scale;
    }

    public void setScale(Integer scale) {
        this.scale = scale;
    }

    ////// OBJECT METHODS

    @Override
    public TypeQualifiers clone() {
        try {
            return (TypeQualifiers) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new Error("Clone is supported", e);
        }
    }
}
