# Project Overview

This project requires building a series of interconnected applications that demonstrate how microledgers can facilitate peer-to-peer payments. There are two key categories of applications:

- **Infrastructure:** The core building blocks that define the architecture.
- **Demo:** Applications that use the infrastructure services to perform transactions with one another for demonstration purposes.

Each application must rigorously adhere to the specifications outlined below. Any deviation must be explicitly documented within the source code using clear comments.

---

# Non Functional Requirements

This is a demo application, and it is acceptable for every component described here to use in-memory databases and data structures instead of production-ready persistence.

---

# Applications

## Infrastructure Applications

Both applications in this category are implemented as Java REST applications using Spring Boot. They should be organized as separate modules within a single Maven multi-module project. Common classes must be factored into reusable modules where applicable.

### Identity Registry

This service maintains an in-memory mapping from application names to their public keys along with the time of registration. It follows a "trust-on-first-use" pattern, meaning that validating the signature with the provided public key during the first use is sufficient to establish trust with the caller.

#### Endpoints

1. **Register Identity**

   - **HTTP Method & URL:**
     ```
     POST /identities/{ID}
     ```
     where `{ID}` is the application’s unique name.

   - **Request Headers:**
     - `X-Signature`: A cryptographic signature of the `timestamp`, generated with the application’s private key.

   - **Request Body (JSON):**
     ```json
     {
       "timestamp": "ISO-8601 timestamp (must be within the past minute)",
       "publicKey": "PEM representation of the public key"
     }
     ```

   - **Behavior:**
     - Verify that the provided timestamp is recent (no older than one minute).
     - Validate the `X-Signature` header by checking the signature against the timestamp using the supplied public key.
     - If successful, store or update the mapping between `{ID}`, the public key, and the registration timestamp.
     - Return an HTTP 201 Created upon successful registration.

   - **Error Responses:**
     - **400 Bad Request:** If the timestamp is invalid or improperly formatted.
     - **401 Unauthorized:** If the signature verification fails.

2. **Get Identity**

   - **HTTP Method & URL:**
     ```
     GET /identities/{ID}
     ```

   - **Response (JSON):**
     ```json
     {
       "publicKey": "PEM representation of the public key",
       "registeredAt": "ISO-8601 timestamp"
     }
     ```

   - **Behavior:**
     - Retrieve and return the corresponding public key and registration timestamp for the given `{ID}`.
     - If no identity exists for `{ID}`, return a 404 Not Found.

   - **Port:**
     - The Identity Registry listens on port **8080**.

---

### Bank

The Bank application manages an in-memory ledger tracking customer accounts and escrow transactions. Customer accounts are linked to demo application identities as registered in the Identity Registry.

Additionally, the Bank maintains its own key pair. Upon startup, the Bank must register itself (with the name `"bank"`) with the Identity Registry.

#### Endpoints

1. **Create Account**

   - **HTTP Method & URL:**
     ```
     PUT /customers/{ID}/account
     ```
     where `{ID}` corresponds to the demo application's name.

   - **Request Headers:**
     - `X-Signature`: A digital signature over the request body, created using the application’s private key.

   - **Request Body (JSON):**
     ```json
     {
       "initialBalance": 100
     }
     ```
     Note: The initial balance should always be a positive numeric value. By default, each demo application begins with an account balance of 100 (interpreted as $100).

   - **Behavior:**
     - Ensure that no account already exists for `{ID}`.
     - Retrieve the public key for `{ID}` from the Identity Registry.
     - Validate the signature on the request body using the retrieved public key.
     - Create the account if all validations pass.

   - **Response:**
     - Return an HTTP 201 Created with the account details in the response body.

   - **Response Body (JSON):**
     ```json
     {
       "owner": "{ID}",
       "balance": 100,
       "createdAt": "ISO-8601 timestamp of account creation"
     }
     ```

   - **Error Responses:**
     - **400 Bad Request:** If the request body or signature header is malformed.
     - **401 Unauthorized:** If signature validation fails.
     - **409 Conflict:** If an account for `{ID}` already exists.

2. **Create Escrow**

   An escrow account is used as proof-of-funds when the account owner attempts to perform a paid transaction with the recipient.

   - **HTTP Method & URL:**
     ```
     POST /customers/{ID}/escrow
     ```

   - **Request Headers:**
     - `X-Signature`: A signature over the JSON body generated by the application’s private key.

   - **Request Body (JSON):**
     ```json
     {
       "recipient": "{RECIPIENT}",
       "amount": 10,
       "activeDurationInSeconds": 30,
       "expirationInSeconds": 10
     }
     ```
     Here, `{RECIPIENT}` is the name of another demo application registered in the Identity Registry. This endpoint initializes an escrow account by deducting the specified `amount` from the sender’s account balance.

   - **Behavior:**
     - Confirm that an account exists for the sender (`{ID}`).
     - Verify that the sender’s account has sufficient funds.
     - Validate that the recipient exists in the Identity Registry.
     - Ensure that both `activeDurationInSeconds` and `expirationInSeconds` are positive numbers.
     - Deduct the escrow `amount` from the sender’s account and record the escrow transaction.
     - Schedule a task that, after a delay calculated as `activeDurationInSeconds` + `expirationInSeconds`, returns any remaining funds from the escrow account back to the sender’s account.

   - **Response:**
     - Return an HTTP 201 Created response that includes the details of the escrow transaction.
     - The response body should be a JSON representation of the escrow account.
     - The response must include a header `X-Proof-of-Funds` that is the base64 encoded genesis block for a microledger maintained by the account owner. This block must contain a Seal that is the hash of the escrow account info and be signed using the Bank’s private key.

   - **Response Body (JSON):**
     ```json
     {
       "escrowId": "randomly generated UUID",
       "sender": "{ID}",
       "recipient": "{RECIPIENT}",
       "amount": 10,
       "createdAt": "ISO-8601 timestamp",
       "activeDurationInSeconds": 30,
       "expirationInSeconds": 10,
       "status": "active"
     }
     ```

   - **Decoded Response Header:**

     The decoded value of the `X-Proof-of-Funds` header should look as follows:
       ```json
       {
         "blockNumber": 1,
         "previousBlockHash": "",
         "digitalFingerprint": "<computed hash>",
         "timeImprint": "<ISO-8601 timestamp>",
         "controllingIdentifiers": [
           {
             "identifierType": "bank",
             "identifierValue": "bank",
             "publicKey": "<bank_public_key>"
           }
         ],
         "seals": [
           {
             "sealType": "SHA-256",
             "sealValue": "<hash of escrow account info>"
           }
         ],
         "signatures": [
           {
             "algorithm": "RSA",
             "value": "<bank signature>"
           }
         ]
       }
       ```

   - **Error Responses:**
     - **400 Bad Request:** For an invalid request body.
     - **401 Unauthorized:** If signature verification fails.
     - **404 Not Found:** If either the sender or recipient is not registered.
     - **422 Unprocessable Entity:** If the sender does not have sufficient funds.

   - **Port:**
     - The Bank application listens on port **8090**.

---

## Demo Applications

Demo applications are implemented in various programming languages (Node.js, Golang, and Python) and are designed to interact with the underlying infrastructure. Each application is classified as either a payer or a payee. For demonstration purposes, each application will serve primarily as one or the other, though in practice they may function as both.

### Payees

Payee applications provide services that require payment. Each incoming API call that carries a fee must include a set of standardized headers and implement operations to secure funds.

**Mandatory HTTP Headers for Paid APIs:**

- **`X-Proof-of-Funds`:** A base64 encoded representation of the Genesis Block from the caller’s escrow account microledger. This Block should include a Seal Attachment element with the original escrow account information, so it can be parsed and validated by the callee.
  *Decoded Example:* (see section under Bank: Create Escrow)

- **`X-Allowance`:** A base64 encoded Block representing the maximum amount the callee is allowed to charge for this request. This Block must include a Seal Attachment element with an unencoded `allowance` JSON object. This Block must be signed by the caller.
  *Decoded Example:*

       ```json
       {
         "blockNumber": 2,
         "previousBlockHash": "<computed hash from previous block>",
         "digitalFingerprint": "<computed hash>",
         "timeImprint": "<ISO-8601 timestamp>",
         "seals": [
           {
             "sealType": "SHA-256",
             "sealValue": "<hash computed over the allowance payload>"
           }
         ],
         "signatures": [
           {
             "algorithm": "RSA",
             "value": "<caller signature>"
           }
         ],
        "sealAttachments": [
          {
            "name": "allowance",
            "value": "base64 encoded Allowance JSON object"
          }
        ]
       }
       ```
     - Example `Allowance` JSON object
     ```json
        {
           "maxCharge": 10,
           "currency": "USD"
         }
     ```

**Behavior for Paid API Calls:**

- Include proof-of-funds (the escrow account’s genesis block).
- Validate that the Block conforms to the microledger specification (validate digital fingerprints and signatures).
- Validate that the escrow account remains active (i.e., the `activeDurationInSeconds` window has not expired).
- Either create or locate a local in-memory microledger representing the transaction history between the caller and callee within the current microledger represented by the proof-of-funds.
- Ensure that the `X-Allowance` header value does not exceed the current ledger balance.
- Ensure that the `X-Allowance` header is the next logical block in the local representation of the microledger (block number matches last block + 1).
- Upon successful processing, the response should include a header named `X-Fee`. This header is a Block including a Seal Attachment with a `fee` object representing how much was spent. It should also include a Seal with the hash of the actual response.
- If the request succeeds, the callee appends the `X-Allowance` block to the end of the local microledger, followed by the `X-Fee` block. The caller does the same.
- Subsequent requests between the same caller and callee will only include the original genesis block (`X-Proof-of-Funds`) and the latest `X-Allowance` (in the request) and `X-Fee` (in the response). It is up to the caller and callee to maintain their own representation of the microledger and ensure it remains valid.

- **Decoded Response Header Examples:**

     - For `X-Allowance` the decoded value example should be:
    TODO document the fee header, as well as the fee json object in the same way that the allowance header was documented.
---

### Sleeper

Sleeper is a Node.js REST application offering a sleep/delay service that charges a fee in proportion to the delay.

- **Type:** Payee
- **Identity:** The application's registered name is `"sleeper"`.

#### Endpoint: Sleep

- **HTTP Method & URL:**
  ```
  GET /sleep?time={seconds}
  ```

- **Query Parameter:**
  - `time` (required): The number of seconds the application should delay the response.

- **Behavior:**
  - Apply the standard payee processing behavior (validate payment headers, perform microledger operations, etc.).
  - Delay the response for the specified number of seconds.
  - Charge the caller $1 per second of delay.
  - Return a response indicating the duration of the sleep and the fee that was deducted.

- **Response Example (JSON):**
  ```json
  {
    "message": "Slept for 5 seconds",
    "feeCharged": 5
  }
  ```

- **Error Handling:**
  - **400 Bad Request:** If the `time` parameter is missing or if its value is not numeric.

---

### Greeter

Greeter is a Golang REST application offering greeting services. It provides two endpoints – one for a greeting message and one for a farewell – with fees determined by the length of the provided name.

- **Type:** Payee
- **Identity:** The application's registered name is `"greeter"`.

#### Endpoints

1. **Hello**

   - **HTTP Method & URL:**
     ```
     GET /hello?name={name}
     ```

   - **Query Parameter:**
     - `name` (required): The text or name to greet.

   - **Behavior:**
     - Apply the standard payee processing behavior (validate payment headers, perform microledger operations, etc.).
     - Return a greeting (e.g., “Hello, {name}!”).
     - Charge $1 per character in the provided name.
     - Follow the standard payee validation and microledger operations.

   - **Example Response (JSON):**
     ```json
     {
       "message": "Hello, Mark!",
       "feeCharged": 4
     }
     ```

2. **Goodbye**

   - **HTTP Method & URL:**
     ```
     GET /goodbye?name={name}
     ```

   - **Query Parameter:**
     - `name` (required): The text or name for the farewell message.

   - **Behavior:**
     - Apply the standard payee processing behavior (validate payment headers, perform microledger operations, etc.).
     - Return a farewell message (e.g., “Goodbye, {name}!”).
     - Charge $1 per character in the provided name.
     - Perform standard payee validation and microledger operations.

   - **Error Handling (Both Endpoints):**
     - **400 Bad Request:** If the `name` parameter is missing.

---

### Agent

The Agent is implemented as a Python script that leverages LangChain and OpenAI. Its purpose is to parse a given instruction string into actionable steps and dynamically invoke either the Sleeper or Greeter endpoints based on those instructions.

- **Type:** Payer
- **Identity:** The application's registered name is `"agent"`.

#### Behavior

- **Input:**
  - A single instruction string, e.g.,
    `"say hi to Mark, wait for 5 seconds, then say goodbye."`

- **Processing:**
  - Parse the instruction into a series of actionable steps.
  - Dynamically determine which external service to invoke:
    - If a greeting (hi or hello) is required, call Greeter’s `/hello` endpoint.
    - For farewells, call Greeter’s `/goodbye` endpoint.
    - For pauses, call Sleeper’s `/sleep` endpoint.
  - Use LangChain and the OpenAI API to determine the best tool to invoke.
  - Ensure that before any tool is invoked, an escrow account is created if necessary.
  - Each API call includes the required payment headers as described above.

- **Output:**
  - The script prints detailed logs or returns a JSON object that outlines every executed step, including the fee charged for each transaction.

---

# Detailed Spec

## Microledger Specification

This section provides a formal specification for a microledger: a self-contained, immutable, append-only log inspired by event sourcing and blockchain principles. A reference Java implementation is provided below; alternative language implementations must adhere to this guide closely. Any deviation must be explicitly documented in the source code.

### Abstract

__Microledger (ML)__ is designed as an immutable, verifiable log of events. It uses cryptographic primitives to support an append-only data structure without requiring global ordering or a peer-to-peer network.

### Detailed Components

#### Blocks

A microledger is constructed as a linked chain of blocks. Each block must contain the following attributes:
- **Block Number:** A sequential number representing the block’s order.
- **Previous Block Hash:** The digital hash (fingerprint) of the previous block. This field is empty for the genesis block.
- **Digital Fingerprint:** A one-way cryptographic hash derived from the canonical serialized block.
- **Time Imprint:** A timestamp (or an external attestation) indicating when the block was generated.
- **Controlling Identifiers:** One or more identifiers (e.g., public keys or DIDs) that define the block’s custodians. These custodians authorize subsequent blocks.
- **Seals:** Cryptographic digests anchoring or referencing external data.
- **Signatures:** One or more cryptographic signatures attesting to the block’s integrity and authenticity. These must verify against the controlling identifiers.

#### Terminology

- **Custodian:** An entity identified by a public key, DID, etc., serving as the owner and validator of a block.
- **Genesis Block:** The first block in a microledger that does not reference any previous block.
- **Multisig:** A methodology requiring multiple custodial signatures to validate a block.
- **SAI:** Self-Addressing Identifier.
- **Seal:** A hash digest used to commit external data.
- **Time Imprint:** An authenticated timestamp asserting the existence of data at a specific time.
- **Digital Fingerprint:** The unique cryptographic hash of a block.
- **Seals Registry/Attachments:** Mechanisms to resolve or retrieve the external data referenced by a seal.

#### Characteristics

- **End Verifiability:**
  Each block is cryptographically chained to the previous one. Authenticity is ensured by verifying that every block’s digital fingerprint is correctly computed and that its signatures match the associated controlling identifiers.

- **Composability:**
  Microledgers are designed to be composable. New genesis blocks may be chained to existing chains if necessary.

- **Ownership Transfer:**
  The ownership of a block is defined by its custodians. To transfer ownership, the current custodians must anchor a new block containing updated controlling identifiers.

- **Serialization and Encoding:**
  Blocks must be self-describing, allowing for exchange across different protocols and supporting pluggable, algorithm-agile serialization mechanisms.

#### Extended Verification

Verification of a microledger proceeds in two phases:
1. **Authenticity:**
   - Verify that each block’s digital fingerprint is computed correctly.
   - For non-genesis blocks, confirm that the block’s fingerprint incorporates the previous block’s digest.
   - Ensure that every signature matches one of the block’s controlling identifiers.

2. **Veracity:**
   - Optionally, validate the reputations and trustworthiness of the custodians through an external governance framework.

#### Ownership Transfer

When transferring ownership:
- The current custodians must anchor a new block that specifies new controlling identifiers.
- The transfer operation must include both the chain of blocks and a mechanism (such as a seals registry) to retrieve externally stored data.
- Transfers may be either synchronous (all data included in one envelope) or asynchronous (with seals arriving apart from the main payload).

### Reference Implementation in Java

The following reference implementation in Java should serve as the canonical guide. Implementations in other languages must match its behavior and structure as closely as possible. Any differences must be clearly noted in the code comments.

#### File: io.agentza.microledger.model.Block

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

#### File: io.agentza.microledger.model.ControllingIdentifier

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

#### File: io.agentza.microledger.model.Microledger

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

#### File: io.agentza.microledger.model.Seal

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

#### File: io.agentza.microledger.model.SealAttachment

```java
package io.agentza.microledger.model;

public class SealAttachment {

    // TODO Implement this class
}
```

#### File: io.agentza.microledger.model.Signature

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

#### File: io.agentza.microledger.service.LedgerService

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

---

# Mapping Table Codes (Example)

The following tables list derivation codes for digital fingerprints, seals, and controlling identifiers. Although these values may be configurable, they must remain consistent across all implementations.

**Digital Fingerprint Representation:**

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

---

# Implementation Guidance Summary

1. **Infrastructure Applications:**
   - Develop applications using Spring Boot in Java.
   - Each HTTP endpoint must perform strict signature and timestamp validation.
   - Use the Identity Registry for identity verification as needed.

2. **Demo Applications:**
   - Use Node.js for Sleeper, Golang for Greeter, and Python for Agent.
   - All endpoints (or external API calls) must include fee calculations and proper microledger support.
   - The Agent must dynamically orchestrate calls using AI (via LangChain and OpenAI) based on parsed instructions.
   - Ensure that the first call to an external tool is preceded by creating an escrow account if required.

3. **Microledger Operations:**
   - Follow the provided Java reference implementation closely.
   - Any deviations between language implementations must be explicitly noted in the code.
   - Ensure that block chaining, digital fingerprint calculation, and signature verification conform to the specification.
