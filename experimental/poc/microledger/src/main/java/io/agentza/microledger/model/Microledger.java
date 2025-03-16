package io.agentza.microledger.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents a single Microledger instance.
 * Each Microledger is composed of a list of Blocks.
 */
public class Microledger {

    private String id; 
    private List<Block> blocks = new ArrayList<>();

    public Microledger() {
        // Generate a random ID or use a more meaningful approach.
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Block> getBlocks() {
        return blocks;
    }

    public void setBlocks(List<Block> blocks) {
        this.blocks = blocks;
    }
}
