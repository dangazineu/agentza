package cmd

import (
	"fmt"
	"github.com/spf13/cobra"
)

// rootCmd is the base command for the CLI
var rootCmd = &cobra.Command{
	Use:   "microledger",
	Short: "A CLI to interact with the Microledger REST app",
	Long:  `Use this CLI to create a Microledger, add blocks, and retrieve ledger details from the Java REST application.`,
}

// Execute is the entry point for the CLI
func Execute() {
	if err := rootCmd.Execute(); err != nil {
		fmt.Println(err)
	}
}

// init is where we add subcommands to the root command
func init() {
	rootCmd.AddCommand(createCmd)
	rootCmd.AddCommand(addBlockCmd)
	rootCmd.AddCommand(getLedgerCmd)
}
