package io.agentza.microledger.model;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Represents a single block within a Microledger.
 */
public class Block {

    // For demonstration, we keep a blockNumber to identify the sequence in the chain.
    private long blockNumber; 

    // Hash/fingerprint of the previous block. Could be set to null/empty for the genesis block.
    private String previousBlockHash;

    // This block's own digital fingerprint
    private String digitalFingerprint;

    // The time imprint is a cryptographic or external attestation, 
    // for simplicity, we store the time the block was created.
    private Instant timeImprint = Instant.now();

    // Controlling identifiers for the block (e.g. public keys, DIDs, etc.)
    private List<ControllingIdentifier> controllingIdentifiers;

    // Seals are cryptographic digests referencing external data
    private List<Seal> seals;

    // A list of signatures from custodians, for demonstration just store as strings or structured objects
    private List<Signature> signatures;

    // Constructors, getters, setters ...

    public long getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(long blockNumber) {
        this.blockNumber = blockNumber;
    }

    public String getPreviousBlockHash() {
        return previousBlockHash;
    }

    public void setPreviousBlockHash(String previousBlockHash) {
        this.previousBlockHash = previousBlockHash;
    }

    public String getDigitalFingerprint() {
        return digitalFingerprint;
    }

    public void setDigitalFingerprint(String digitalFingerprint) {
        this.digitalFingerprint = digitalFingerprint;
    }

    public Instant getTimeImprint() {
        return timeImprint;
    }

    public void setTimeImprint(Instant timeImprint) {
        this.timeImprint = timeImprint;
    }

    public List<ControllingIdentifier> getControllingIdentifiers() {
        return controllingIdentifiers;
    }

    public void setControllingIdentifiers(List<ControllingIdentifier> controllingIdentifiers) {
        this.controllingIdentifiers = controllingIdentifiers;
    }

    public List<Seal> getSeals() {
        return seals;
    }

    public void setSeals(List<Seal> seals) {
        this.seals = seals;
    }

    public List<Signature> getSignatures() {
        return signatures;
    }

    public void setSignatures(List<Signature> signatures) {
        this.signatures = signatures;
    }

    @Override
    public String toString() {
        return canonicalBlockString(this);
    }

    // TODO add Seals to the canonical string representation
    private String canonicalBlockString(Block block) {
        StringBuilder sb = new StringBuilder();
        sb.append("{")
                .append("\"blockNumber\":").append(block.getBlockNumber()).append(",")
                .append("\"previousBlockHash\":\"").append(nullSafe(block.getPreviousBlockHash())).append("\",")
                .append("\"timeImprint\":\"").append(DateTimeFormatter.ISO_INSTANT.format(block.getTimeImprint())).append("\",");
        sb.append("}");
        return sb.toString();
    }

    private String nullSafe(String value) {
        return value == null ? "" : value;
    }
}
