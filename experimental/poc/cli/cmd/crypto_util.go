// file: crypto_util.go

package cmd

import (
	"crypto"
	"crypto/rand"
	"crypto/rsa"
	"crypto/sha256"
	"crypto/x509"
	"encoding/base64"
	"encoding/pem"
	"errors"
	"fmt"
	"io/ioutil"
	"os"
)

// If key.pem exists, parse it; otherwise generate a new RSA key and write it to file.
func LoadOrGenerateRSAKey() (*rsa.PrivateKey, error) {
	if _, err := os.Stat("key.pem"); err == nil {
		// File exists, load it
		return loadRSAPrivateKeyFromFile("key.pem")
	} else {
		// Generate a new key
		privKey, err := rsa.GenerateKey(rand.Reader, 2048)
		if err != nil {
			return nil, err
		}
		// Save to file
		if err = saveRSAPrivateKeyToFile("key.pem", privKey); err != nil {
			return nil, err
		}
		// Optionally also save the public key
		if err = saveRSAPublicKeyToFile("pub.pem", &privKey.PublicKey); err != nil {
			return nil, err
		}
		return privKey, nil
	}
}

func loadRSAPrivateKeyFromFile(filepath string) (*rsa.PrivateKey, error) {
	data, err := ioutil.ReadFile(filepath)
	if err != nil {
		return nil, err
	}
	block, _ := pem.Decode(data)
	if block == nil || block.Type != "RSA PRIVATE KEY" {
		return nil, errors.New("not a valid PEM-encoded RSA private key")
	}
	priv, err := x509.ParsePKCS1PrivateKey(block.Bytes)
	if err != nil {
		return nil, err
	}
	return priv, nil
}

func saveRSAPrivateKeyToFile(filepath string, priv *rsa.PrivateKey) error {
	block := &pem.Block{
		Type:  "RSA PRIVATE KEY",
		Bytes: x509.MarshalPKCS1PrivateKey(priv),
	}
	return ioutil.WriteFile(filepath, pem.EncodeToMemory(block), 0600)
}

func saveRSAPublicKeyToFile(filepath string, pub *rsa.PublicKey) error {
	pubBytes, err := x509.MarshalPKIXPublicKey(pub)
	if err != nil {
		return err
	}
	block := &pem.Block{
		Type:  "PUBLIC KEY",
		Bytes: pubBytes,
	}
	return ioutil.WriteFile(filepath, pem.EncodeToMemory(block), 0644)
}

// signBlockFingerprint uses RSA to sign the canonical representation’s SHA-256 hash.
func signBlockFingerprint(privKey *rsa.PrivateKey, fingerprint string) (string, error) {
	//// 1. Build the canonical string (MUST match Java side).
	//canon := canonicalBlockString(block)

	// 2. Compute SHA-256 of that string
	hash := sha256.Sum256([]byte(fingerprint))

	// 3. Sign
	sigBytes, err := rsa.SignPKCS1v15(rand.Reader, privKey, crypto.SHA256, hash[:])
	if err != nil {
		return "", fmt.Errorf("error signing block: %v", err)
	}

	// Return base64-encoded signature
	return base64.StdEncoding.EncodeToString(sigBytes), nil
}
