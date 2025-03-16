package cmd

import (
	"fmt"
	"strings"
	"time"

	"agentza.io/example/model"
)

func canonicalBlockString(block *model.Block) string {
	// Example only. Real code: do a canonical JSON, sorted keys, etc.
	var sb strings.Builder
	sb.WriteString("{")
	sb.WriteString(fmt.Sprintf("\"blockNumber\":%d,", block.BlockNumber))
	if block.PreviousBlockHash == nil {
		sb.WriteString("\"previousBlockHash\":\"\",")
	} else {
		sb.WriteString(fmt.Sprintf("\"previousBlockHash\":\"%s\",", *block.PreviousBlockHash))
	}
	sb.WriteString(fmt.Sprintf("\"timeImprint\":\"%s\",", block.TimeImprint.Format(time.RFC3339Nano)))
	sb.WriteString("}")
	return sb.String()
}
