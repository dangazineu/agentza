package io.agentza.microledger.model;

/**
 * Represents a cryptographic signature from a Custodian or group of Custodians.
 */
public class Signature {

    // E.g., "RSA", "EC" (ECDSA), or "Ed25519"
    private String algorithm;

    // Actual signature value (e.g. base64-encoded)
    private String value;

    public Signature() {}

    public Signature(String algorithm, String value) {
        this.algorithm = algorithm;
        this.value = value;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
