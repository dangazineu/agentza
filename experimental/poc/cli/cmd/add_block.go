// file: add_block.go
package cmd

import (
	"bytes"
	"crypto/rsa"
	"crypto/sha256"
	"crypto/x509"
	"encoding/json"
	"encoding/pem"
	"fmt"
	"io/ioutil"
	"log"
	"net/http"
	"time"

	"agentza.io/example/model"
	"github.com/spf13/cobra"
)

var (
	addBlockLedgerID string
	addBlockBaseURL  string
)

var addBlockCmd = &cobra.Command{
	Use:   "add-block",
	Short: "Add a cryptographically signed block to an existing Microledger",
	Long: `This command fetches the existing ledger state, 
constructs a new block with an RSA signature, 
and sends it to /api/ledger/{ledgerId}/blocks.`,
	Run: func(cmd *cobra.Command, args []string) {
		if addBlockLedgerID == "" {
			log.Fatal("Missing --ledger-id")
		}

		lastBlockNumber, lastBlockHash, err := getLastBlockInfo(addBlockLedgerID)
		if err != nil {
			log.Fatalf("Error getting last block info: %v", err)
		}

		blockNumber := lastBlockNumber + 1
		newBlock := &model.Block{
			BlockNumber:       blockNumber,
			PreviousBlockHash: nil,
			TimeImprint:       time.Now().UTC(),
		}
		if lastBlockHash != "" {
			newBlock.PreviousBlockHash = &lastBlockHash
		}

		// 4. We can attempt to compute the digital fingerprint here.
		// Actually we need to store it in newBlock.DigitalFingerprint before signing or we can do it after.
		// But the server expects that we pass digitalFingerprint in the request,
		// and the signature is over that canonical form.
		// So let's do it in two steps:
		//   a) compute canonical representation ignoring DigitalFingerprint
		//   b) sign that representation
		//   c) store the computed hash in DigitalFingerprint
		// For simplicity, we replicate the Java approach:
		// the block's "canonical representation" doesn't depend on DigitalFingerprint itself.
		// Then we do the sign.
		// Then we embed the resulting signature.

		// We'll do the "final" canonical representation in the signBlockFingerprint function
		// which also includes blockNumber, previousBlockHash, timeImprint, etc.

		// 5. Create controlling identifier with our public key in PEM

		// Maybe we include a sample seal for demonstration
		newBlock.Seals = []model.Seal{
			{
				SealType:  "SHA-256",
				SealValue: "abcd1234",
			},
		}

		// Now we have a signature over the "canonical representation" (which didn't involve digitalFingerprint).
		// We also want to store the digitalFingerprint the same way the server does:
		// a SHA-256 of that canonical representation. We'll replicate that here:
		cannonicalBlockString := canonicalBlockString(newBlock)
		localFingerprint, err := computeSHA256Hex(cannonicalBlockString)

		fmt.Printf("Canonical representation: %s \n", cannonicalBlockString)
		fmt.Printf("Fingerprint: %s \n", localFingerprint)

		if err != nil {
			log.Fatalf("Error computing local fingerprint: %v", err)
		}
		newBlock.DigitalFingerprint = &localFingerprint

		privKey, err := LoadOrGenerateRSAKey()
		if err != nil {
			log.Fatalf("Error loading/generating RSA key: %v", err)
		}

		sigValue, err := signBlockFingerprint(privKey, localFingerprint)
		if err != nil {
			log.Fatalf("Error signing block: %v", err)
		}

		// Store the signature
		newBlock.Signatures = []model.Signature{
			{
				Algorithm: "RSA", // The server maps "RSA" -> "SHA256withRSA"
				Value:     sigValue,
			},
		}

		pubPEM, err := getPublicKeyPEM(&privKey.PublicKey)
		if err != nil {
			log.Fatalf("Error encoding public key: %v", err)
		}

		newBlock.ControllingIdentifiers = []model.ControllingIdentifier{
			{
				IdentifierType:  "RSA",
				IdentifierValue: "ExampleRSACustodian",
				PublicKey:       pubPEM, // So the server can verify signature
			},
		}

		// 6. POST the block to the server
		err = postBlock(addBlockLedgerID, newBlock)
		if err != nil {
			log.Fatalf("Error posting block: %v", err)
		}

		fmt.Println("Block successfully added!")
	},
}

func init() {
	addBlockCmd.Flags().StringVarP(&addBlockLedgerID, "ledger-id", "l", "", "Ledger ID to which we add a block")
	addBlockCmd.Flags().StringVar(&addBlockBaseURL, "url", "http://localhost:8080", "Base URL of the Microledger REST application")
}

func getLastBlockInfo(ledgerID string) (int64, string, error) {
	// GET /api/ledger/{ledgerId}, parse JSON
	url := fmt.Sprintf("%s/api/ledger/%s", addBlockBaseURL, ledgerID)
	resp, err := http.Get(url)
	if err != nil {
		return 0, "", err
	}
	defer resp.Body.Close()
	if resp.StatusCode != 200 {
		return 0, "", fmt.Errorf("unexpected status code: %d", resp.StatusCode)
	}

	var ledgerResp struct {
		ID     string        `json:"id"`
		Blocks []model.Block `json:"blocks"`
	}
	if err := json.NewDecoder(resp.Body).Decode(&ledgerResp); err != nil {
		return 0, "", err
	}

	if len(ledgerResp.Blocks) == 0 {
		// no blocks yet => genesis
		return 0, "", nil
	}
	lastBlock := ledgerResp.Blocks[len(ledgerResp.Blocks)-1]
	fingerprint := ""
	if lastBlock.DigitalFingerprint != nil {
		fingerprint = *lastBlock.DigitalFingerprint
	}
	return lastBlock.BlockNumber, fingerprint, nil
}

func postBlock(ledgerID string, block *model.Block) error {
	url := fmt.Sprintf("%s/api/ledger/%s/blocks", addBlockBaseURL, ledgerID)
	bodyBytes, err := json.Marshal(block)
	if err != nil {
		return err
	}
	req, err := http.NewRequest("POST", url, bytes.NewReader(bodyBytes))
	if err != nil {
		return err
	}
	req.Header.Set("Content-Type", "application/json")

	client := &http.Client{}
	resp, err := client.Do(req)
	if err != nil {
		return err
	}
	defer resp.Body.Close()

	if resp.StatusCode != 200 {
		data, _ := ioutil.ReadAll(resp.Body)
		return fmt.Errorf("Server returned status %d: %s", resp.StatusCode, string(data))
	}
	return nil
}

// computeSHA256Hex calculates the SHA-256 hash of a string, returns hex-encoded value.
func computeSHA256Hex(data string) (string, error) {
	h := sha256.New()
	_, err := h.Write([]byte(data))
	if err != nil {
		return "", err
	}
	sum := h.Sum(nil)
	return fmt.Sprintf("%x", sum), nil
}

// getPublicKeyPEM returns a PEM-encoded public key (PKIX).
func getPublicKeyPEM(pub *rsa.PublicKey) (string, error) {
	pubBytes, err := x509.MarshalPKIXPublicKey(pub)
	if err != nil {
		return "", err
	}
	block := &pem.Block{
		Type:  "PUBLIC KEY",
		Bytes: pubBytes,
	}
	return string(pem.EncodeToMemory(block)), nil
}
