package io.agentza.microledger.controller;

import io.agentza.microledger.model.Block;
import io.agentza.microledger.model.Microledger;
import io.agentza.microledger.service.LedgerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ledger")
public class LedgerController {

    private final LedgerService ledgerService;

    public LedgerController(LedgerService ledgerService) {
        this.ledgerService = ledgerService;
    }

    /**
     * POST /api/ledger
     * Creates a new ledger and returns it.
     */
    @PostMapping
    public ResponseEntity<Microledger> createNewLedger() {
        Microledger newLedger = ledgerService.createLedger();
        return ResponseEntity.ok(newLedger);
    }

    /**
     * POST /api/ledger/{ledgerId}/blocks
     * Adds a new block to a ledger with the given ID and returns the block.
     */
    @PostMapping("/{ledgerId}/blocks")
    public ResponseEntity<Block> addBlock(
            @PathVariable String ledgerId,
            @RequestBody Block blockRequest
    ) {

        System.out.println("Received block request: " + blockRequest);
        System.out.println("Received fingerprint: " + blockRequest.getDigitalFingerprint());

        Block createdBlock = ledgerService.addBlock(ledgerId, blockRequest);
        return ResponseEntity.ok(createdBlock);
    }

    /**
     * (Optional) GET /api/ledger/{ledgerId}
     * Retrieve and return the ledger by ID, including all blocks.
     */
    @GetMapping("/{ledgerId}")
    public ResponseEntity<Microledger> getLedger(@PathVariable String ledgerId) {
        Microledger ledger = ledgerService.getLedger(ledgerId);
        if (ledger == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ledger);
    }
}
