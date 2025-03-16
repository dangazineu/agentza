package io.agentza.microledger.model;

/**
 * Represents any entity that has control/ownership over the Microledger,
 * e.g., a public key, DID, KERI prefix, etc.
 */
public class ControllingIdentifier {

    // e.g., "PublicKey", "DID:web", "KERI" ...
    private String identifierType;

    // e.g., the actual DID or public key. For demonstration, we’ll store the public key in base64 or PEM.
    private String identifierValue;

    // (Optional) If using standard public keys, you can store the raw public key as base64 or a PEM string here.
    private String publicKey;

    public ControllingIdentifier() {}

    public ControllingIdentifier(String identifierType, String identifierValue, String publicKey) {
        this.identifierType = identifierType;
        this.identifierValue = identifierValue;
        this.publicKey = publicKey;
    }

    public String getIdentifierType() {
        return identifierType;
    }

    public void setIdentifierType(String identifierType) {
        this.identifierType = identifierType;
    }

    public String getIdentifierValue() {
        return identifierValue;
    }

    public void setIdentifierValue(String identifierValue) {
        this.identifierValue = identifierValue;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }
}
