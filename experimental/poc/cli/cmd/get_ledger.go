package cmd

import (
	"encoding/json"
	"fmt"
	"log"
	"net/http"

	"github.com/spf13/cobra"
)

var (
	getLedgerID   string
	getLedgerBase string
)

var getLedgerCmd = &cobra.Command{
	Use:   "get-ledger",
	Short: "Retrieve a Microledger by ID",
	Long:  `Sends a GET request to /api/ledger/{ledgerId} to retrieve ledger info.`,
	Run: func(cmd *cobra.Command, args []string) {
		if getLedgerID == "" {
			log.Fatal("Missing --ledger-id")
		}

		// Construct the URL
		url := fmt.Sprintf("%s/api/ledger/%s", getLedgerBase, getLedgerID)

		resp, err := http.Get(url)
		if err != nil {
			log.Fatalf("Error retrieving ledger: %v\n", err)
		}
		defer resp.Body.Close()

		if resp.StatusCode != http.StatusOK {
			log.Fatalf("Failed to retrieve ledger. HTTP Status: %d\n", resp.StatusCode)
		}

		// Decode into an interface or a struct
		var ledgerResp map[string]interface{}
		if err := json.NewDecoder(resp.Body).Decode(&ledgerResp); err != nil {
			log.Fatalf("Error decoding ledger response: %v\n", err)
		}

		fmt.Println("Ledger retrieved successfully:")
		b, _ := json.MarshalIndent(ledgerResp, "", "  ")
		fmt.Println(string(b))
	},
}

func init() {
	getLedgerCmd.Flags().StringVarP(&getLedgerID, "ledger-id", "l", "", "Ledger ID to retrieve")
	getLedgerCmd.Flags().StringVar(&getLedgerBase, "url", "http://localhost:8080", "Base URL of the Microledger REST application")
}
