// file: model/block.go
package model

import (
	"time"
)

type Block struct {
	BlockNumber            int64                   `json:"blockNumber"`
	PreviousBlockHash      *string                 `json:"previousBlockHash,omitempty"`
	DigitalFingerprint     *string                 `json:"digitalFingerprint,omitempty"`
	TimeImprint            time.Time               `json:"timeImprint"`
	ControllingIdentifiers []ControllingIdentifier `json:"controllingIdentifiers,omitempty"`
	Seals                  []Seal                  `json:"seals,omitempty"`
	Signatures             []Signature             `json:"signatures,omitempty"`
}

type ControllingIdentifier struct {
	IdentifierType  string `json:"identifierType"`
	IdentifierValue string `json:"identifierValue"`
	PublicKey       string `json:"publicKey"` // PEM or base64
}

type Seal struct {
	SealType  string `json:"sealType"`
	SealValue string `json:"sealValue"`
}

type Signature struct {
	Algorithm string `json:"algorithm"`
	Value     string `json:"value"` // base64 signature
}
