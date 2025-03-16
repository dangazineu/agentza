package cmd

import (
	"encoding/json"
	"fmt"
	"log"
	"net/http"

	"github.com/spf13/cobra"
)

// createCmd represents the command to create a new microledger
var createCmd = &cobra.Command{
	Use:   "create",
	Short: "Create a new Microledger",
	Long:  `Sends a POST request to /api/ledger to create a new Microledger.`,
	Run: func(cmd *cobra.Command, args []string) {
		// Build the request URL: e.g. http://localhost:8080/api/ledger
		url := fmt.Sprintf("%s/api/ledger", baseURL)

		// Make the POST request with no body (the server side does the creation)
		resp, err := http.Post(url, "application/json", nil)
		if err != nil {
			log.Fatalf("Error creating ledger: %v\n", err)
		}
		defer resp.Body.Close()

		if resp.StatusCode != http.StatusOK {
			log.Fatalf("Failed to create ledger. HTTP Status: %d\n", resp.StatusCode)
		}

		// Decode the response JSON into a map (or struct)
		var ledgerResp map[string]interface{}
		if err := json.NewDecoder(resp.Body).Decode(&ledgerResp); err != nil {
			log.Fatalf("Error decoding response: %v\n", err)
		}

		// Print ledger details
		fmt.Println("New Ledger created:")
		b, _ := json.MarshalIndent(ledgerResp, "", "  ")
		fmt.Println(string(b))
	},
}

// baseURL is the server endpoint for the microledger REST API
var baseURL string

func init() {
	// Let the user override the base URL via a flag
	createCmd.Flags().StringVar(&baseURL, "url", "http://localhost:8080", "Base URL of the Microledger REST application")
}
