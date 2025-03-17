# Microledger Specification

This document provides the formal specification of a Microledger (ML): an immutable, verifiable, append-only log inspired by event sourcing and blockchain principles. All implementations must adhere closely to this guide.

## Abstract

Microledger (ML) is an immutable log of events. It utilizes cryptographic primitives for an append-only structure without requiring global ordering or peer-to-peer networking.

## Components

### Blocks

A microledger consists of a chain of blocks. Each block includes:
- **Block Number:** Sequential order of the block.
- **Previous Block Hash:** Digital hash of the previous block (empty for genesis block).
- **Digital Fingerprint:** One-way cryptographic hash derived from the canonical serialized block.
- **Time Imprint:** Timestamp indicating when the block was generated.
- **Controlling Identifiers:** One or more identifiers (e.g., public keys or DIDs) authorizing subsequent blocks.
- **Seals:** Cryptographic digests anchoring external data.
- **Signatures:** Signatures attesting to the block’s integrity and authenticity.

### Terminology

- **Custodian:** Entity (public key, DID, etc.) that owns and validates a block.
- **Genesis Block:** The first block in the chain (has no previous block hash).
- **Multisig:** Method requiring multiple custodial signatures.
- **SAI:** Self-Addressing Identifier.
- **Seal:** Hash digest used to commit external data.
- **Time Imprint:** Authenticated timestamp confirming data existence.
- **Digital Fingerprint:** Unique cryptographic hash of a block.
- **Seal Attachments:** Mechanisms to retrieve externally stored data referenced by a seal.

### Characteristics

- **End Verifiability:**
  - Blocks are cryptographically chained.
  - Authenticity is ensured by verifying that each block’s digital fingerprint is correct and that its signatures match the controlling identifiers.
- **Composability:**
  - Microledgers can be composed together. New genesis blocks may be chained to existing chains if required.
- **Ownership Transfer:**
  - Ownership is defined by custodians. To transfer ownership, current custodians append a new block with updated controlling identifiers.
- **Serialization and Encoding:**
  - Blocks must be self-describing and support algorithm-agile serialization.

### Extended Verification

Verification proceeds in two phases:
1. **Authenticity:**
   - Verify the digital fingerprint of every block.
   - For non-genesis blocks, ensure the previous block’s digest is included.
   - Confirm each signature against the block’s controlling identifiers.
2. **Veracity:**
   - Optionally, validate the trustworthiness of the custodians via an external governance framework.

### Ownership Transfer

- The current custodians must anchor a new block specifying the new controlling identifiers.
- The transfer operation must include the chain of blocks and a mechanism (e.g., a seals registry) for retrieving externally stored data.
- Transfers may be synchronous (all data included) or asynchronous.

## Reference Implementation in Java

Below is the canonical Java implementation reference. All language implementations must adhere to this behavior and structure.

### File: io.agentza.microledger.model.Block
```java
package io.agentza.microledger.model;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Block {
    private long blockNumber;
    private String previousBlockHash;
    private String digitalFingerprint;
    private Instant timeImprint = Instant.now();
    private List<ControllingIdentifier> controllingIdentifiers;
    private List<Seal> seals;
    private List<Signature> signatures;

    // Getters and setters...

    @Override
    public String toString() {
        return canonicalBlockString(this);
    }

    private String canonicalBlockString(Block block) {
        StringBuilder sb = new StringBuilder();
        sb.append("{")
          .append("\"blockNumber\":").append(block.getBlockNumber()).append(",")
          .append("\"previousBlockHash\":\"").append(nullSafe(block.getPreviousBlockHash())).append("\",")
          .append("\"timeImprint\":\"").append(DateTimeFormatter.ISO_INSTANT.format(block.getTimeImprint())).append("\"");
        // TODO: Append seals and controlling identifiers in canonical form.
        sb.append("}");
        return sb.toString();
    }

    private String nullSafe(String value) {
        return value == null ? "" : value;
    }
}
```

### File: io.agentza.microledger.model.ControllingIdentifier
```java
package io.agentza.microledger.model;

public class ControllingIdentifier {
    private String identifierType;
    private String identifierValue;
    private String publicKey;

    public ControllingIdentifier() {}

    public ControllingIdentifier(String identifierType, String identifierValue, String publicKey) {
        this.identifierType = identifierType;
        this.identifierValue = identifierValue;
        this.publicKey = publicKey;
    }

    // Getters and setters...
}
```

### File: io.agentza.microledger.model.Microledger
```java
package io.agentza.microledger.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Microledger {
    private String id;
    private List<Block> blocks = new ArrayList<>();

    public Microledger() {
        this.id = UUID.randomUUID().toString();
    }

    // Getters and setters...
}
```

### File: io.agentza.microledger.model.Seal
```java
package io.agentza.microledger.model;

public class Seal {
    private String sealType;   // e.g., "SHA-256", "SAI"
    private String sealValue;  // The cryptographic digest value

    public Seal() {}

    public Seal(String sealType, String sealValue) {
        this.sealType = sealType;
        this.sealValue = sealValue;
    }

    // Getters and setters...
}
```

### File: io.agentza.microledger.model.SealAttachment
```java
package io.agentza.microledger.model;

public class SealAttachment {
    private String name;  // e.g., "allowance", "fee"
    private String value; // Base64 encoded attachment value

    public SealAttachment() {}

    public SealAttachment(String name, String value) {
        this.name = name;
        this.value = value;
    }

    // Getters and setters...
}
```

### File: io.agentza.microledger.model.Signature
```java
package io.agentza.microledger.model;

public class Signature {
    private String algorithm; // e.g., "RSA", "EC", "Ed25519"
    private String value;     // Base64-encoded signature

    public Signature() {}

    public Signature(String algorithm, String value) {
        this.algorithm = algorithm;
        this.value = value;
    }

    // Getters and setters...
}
```

### File: io.agentza.microledger.service.LedgerService
```java
package io.agentza.microledger.service;

import io.agentza.microledger.model.Block;
import io.agentza.microledger.model.ControllingIdentifier;
import io.agentza.microledger.model.Microledger;
import io.agentza.microledger.model.Signature;
import org.springframework.stereotype.Service;

import java.security.PublicKey;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LedgerService {
    private final Map<String, Microledger> ledgers = new ConcurrentHashMap<>();

    public Microledger createLedger() {
        Microledger ledger = new Microledger();
        ledgers.put(ledger.getId(), ledger);
        return ledger;
    }

    public Microledger getLedger(String ledgerId) {
        return ledgers.get(ledgerId);
    }

    public Block addBlock(String ledgerId, Block newBlock) {
        Microledger ledger = ledgers.get(ledgerId);
        if (ledger == null) {
            throw new IllegalArgumentException("Ledger with ID " + ledgerId + " not found");
        }
        newBlock.setBlockNumber(ledger.getBlocks().size() + 1);
        if (newBlock.getBlockNumber() > 1) {
            Block previousBlock = ledger.getBlocks().get(ledger.getBlocks().size() - 1);
            newBlock.setPreviousBlockHash(previousBlock.getDigitalFingerprint());
        }
        String computedHash = computeBlockHash(newBlock);
        if (!computedHash.equals(newBlock.getDigitalFingerprint())) {
            throw new RuntimeException("Provided digitalFingerprint does not match computed block hash!");
        }
        verifySignatures(newBlock);
        ledger.getBlocks().add(newBlock);
        return newBlock;
    }

    private String computeBlockHash(Block block) {
        try {
            String canonical = block.toString();
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(canonical.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return bytesToHex(digest);
        } catch (Exception e) {
            throw new RuntimeException("Error computing block hash", e);
        }
    }

    private String bytesToHex(byte[] bytes) {
       StringBuilder sb = new StringBuilder();
       for (byte b : bytes) {
          sb.append(String.format("%02x", b));
       }
       return sb.toString();
    }

    private void verifySignatures(Block block) {
        String messageToVerify = block.getDigitalFingerprint();
        for (Signature sig : block.getSignatures()) {
            boolean valid = false;
            for (ControllingIdentifier cid : block.getControllingIdentifiers()) {
                if (cid.getPublicKey() != null && !cid.getPublicKey().isEmpty()) {
                    valid = verifySignature(cid, sig, messageToVerify);
                    if (valid) break;
                }
            }
            if (!valid) {
                throw new RuntimeException("Signature verification failed for signature: " + sig.getValue());
            }
        }
    }

    private boolean verifySignature(ControllingIdentifier cid, Signature signature, String message) {
        try {
            java.security.PublicKey pubKey = parsePublicKeyFromPemOrBase64(cid.getPublicKey());
            String algo = mapToJavaSignatureAlgorithm(signature.getAlgorithm());
            java.security.Signature sigInstance = java.security.Signature.getInstance(algo);
            sigInstance.initVerify(pubKey);
            sigInstance.update(message.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            byte[] signatureBytes = decodeSignatureValue(signature.getValue());
            return sigInstance.verify(signatureBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private PublicKey parsePublicKeyFromPemOrBase64(String pubKeyEncoded) throws Exception {
         String sanitized = pubKeyEncoded
             .replace("-----BEGIN PUBLIC KEY-----", "")
             .replace("-----END PUBLIC KEY-----", "")
             .replaceAll("\\s+", "");
         byte[] decodedKey = java.util.Base64.getDecoder().decode(sanitized);
         java.security.spec.X509EncodedKeySpec keySpec = new java.security.spec.X509EncodedKeySpec(decodedKey);
         java.security.KeyFactory keyFactory = java.security.KeyFactory.getInstance("RSA");
         return keyFactory.generatePublic(keySpec);
    }

    private String mapToJavaSignatureAlgorithm(String algo) {
         switch (algo.toUpperCase()) {
             case "RSA": return "SHA256withRSA";
             case "EC":
             case "ECDSA": return "SHA256withECDSA";
             case "ED25519": return "Ed25519"; // Requires Java 15+
             default: return "SHA256withRSA";
         }
    }

    private byte[] decodeSignatureValue(String signatureVal) {
         return java.util.Base64.getDecoder().decode(signatureVal);
    }
}
```

## Mapping Table Codes (Example)

| Concept   | Code |
|-----------|------|
| SAI       | A    |
| Multihash | B    |

**Seal Representation:**

| Concept   | Code |
|-----------|------|
| SAI       | A    |
| Multihash | B    |

**Controlling Identifiers Representation:**

| Concept                                | Code |
|----------------------------------------|------|
| Self-certifying basic prefix           | A    |
| Self-certifying self-addressing prefix | B    |
| Self-certifying self-signing prefix    | C    |
| DID:peer                               | D    |
| DID:web                                | E    |