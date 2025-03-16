package io.agentza.microledger.model;

/**
 * Represents a cryptographic commitment or digest to external data.
 */
public class Seal {

    private String sealType;   // e.g., "SHA-256", "SAI", "Multihash"
    private String sealValue;  // The actual digest string

    public Seal() {
    }

    public Seal(String sealType, String sealValue) {
        this.sealType = sealType;
        this.sealValue = sealValue;
    }

    public String getSealType() {
        return sealType;
    }

    public void setSealType(String sealType) {
        this.sealType = sealType;
    }

    public String getSealValue() {
        return sealValue;
    }

    public void setSealValue(String sealValue) {
        this.sealValue = sealValue;
    }
}
