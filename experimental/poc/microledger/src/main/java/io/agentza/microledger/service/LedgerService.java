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

    // In-memory registry of ledgers: ledgerId -> Microledger
    private final Map<String, Microledger> ledgers = new ConcurrentHashMap<>();

    /**
     * Create a new Microledger instance and store it in memory.
     */
    public Microledger createLedger() {
        Microledger ledger = new Microledger();
        ledgers.put(ledger.getId(), ledger);
        return ledger;
    }

    /**
     * Fetch a ledger by its ID (or return null if none found).
     */
    public Microledger getLedger(String ledgerId) {
        return ledgers.get(ledgerId);
    }

    /**
     * Add a new block to the ledger with the given ID.
     */
    public Block addBlock(String ledgerId, Block newBlock) {
        Microledger ledger = ledgers.get(ledgerId);
        if (ledger == null) {
            throw new IllegalArgumentException("Ledger with ID " + ledgerId + " not found");
        }

        // For demonstration, blockNumber = existing size + 1
        newBlock.setBlockNumber(ledger.getBlocks().size() + 1);

        // Set the previous block's hash if this is not the genesis block
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
                    if (valid) {
                        break;
                    }
                }
            }
            if (!valid) {
                throw new RuntimeException("Signature verification failed for signature: " + sig.getValue());
            }
        }
    }

    /**
     * Attempt to verify the signature using the publicKey in ControllingIdentifier.
     * We assume RSA or ECDSA for demonstration.
     * For Ed25519, you'd likely need BouncyCastle or a Java version that supports it.
     */
    private boolean verifySignature(ControllingIdentifier cid, Signature signature, String message) {
        try {
            // 1. Parse the public key from cid.getPublicKey().
            //    For example, if it's a Base64-encoded X.509 or a PEM file, you'd parse it with the relevant Java calls.

            java.security.PublicKey pubKey = parsePublicKeyFromPemOrBase64(cid.getPublicKey());

            // 2. Create a Signature instance with the specified algorithm (e.g. "SHA256withRSA" or "SHA256withECDSA").
            //    The spec's "algorithm" might just be "RSA" or "EC"; we'd likely do "SHA256with" + something.
            String algo = mapToJavaSignatureAlgorithm(signature.getAlgorithm());
            java.security.Signature sigInstance = java.security.Signature.getInstance(algo);
            sigInstance.initVerify(pubKey);
            sigInstance.update(message.getBytes(java.nio.charset.StandardCharsets.UTF_8));

            // 3. Decode the signature from base64 or hex
            byte[] signatureBytes = decodeSignatureValue(signature.getValue());

            // 4. Verify
            return sigInstance.verify(signatureBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private PublicKey parsePublicKeyFromPemOrBase64(String pubKeyEncoded) throws Exception {
        /*
         * The pubKeyEncoded string is expected to contain something like:
         *
         *     -----BEGIN PUBLIC KEY-----
         *     MIIBIjANBgkqhkiG9w0BAQEFA...
         *     -----END PUBLIC KEY-----
         *
         * or it might be all in one line. We'll strip out the headers and newlines, then decode from Base64.
         * Finally, we'll parse it as an X.509-encoded RSA public key.
         */

        // 1. Remove PEM armor (header/footer) and any whitespace
        String sanitized = pubKeyEncoded
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", ""); // remove newlines and spaces

        // 2. Base64-decode the key bytes
        byte[] decodedKey = java.util.Base64.getDecoder().decode(sanitized);

        // 3. Create a key spec (X.509 for RSA public key)
        java.security.spec.X509EncodedKeySpec keySpec = new java.security.spec.X509EncodedKeySpec(decodedKey);

        // 4. Generate a PublicKey using KeyFactory.
        //    If you are strictly using RSA, use "RSA".
        //    For ECDSA, you'd do "EC", etc.
        java.security.KeyFactory keyFactory = java.security.KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }


    private String mapToJavaSignatureAlgorithm(String algo) {
        // If user supplies "RSA", we might interpret that as "SHA256withRSA"
        // If user supplies "EC", we might interpret that as "SHA256withECDSA"
        // If user supplies "Ed25519", we might interpret as "Ed25519" (Java 15+)
        // This is a simple placeholder:
        switch (algo.toUpperCase()) {
            case "RSA":
                return "SHA256withRSA";
            case "EC":
            case "ECDSA":
                return "SHA256withECDSA";
            case "ED25519":
                return "Ed25519"; // Java 15+
            default:
                return "SHA256withRSA";
        }
    }

    private byte[] decodeSignatureValue(String signatureVal) {
        // If your signature is in base64:
        return java.util.Base64.getDecoder().decode(signatureVal);
    }


}
