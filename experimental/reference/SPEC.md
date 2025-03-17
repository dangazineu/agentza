# Project Specification

This repository is organized into high-level domains that clearly separate the core infrastructure services, the reusable common components, and the demo applications that showcase peer-to-peer payment interactions using microledgers.

## Overview

The project demonstrates how microledgers facilitate secure, verifiable, peer-to-peer payments. It is divided into the following categories:

1. **Infrastructure Applications** (Java Spring Boot):
   - **Identity Registry:** Maintains an in-memory mapping of application names to their public keys and registration timestamps using a trust-on-first-use pattern. (See /infrastructure/identity-registry/SPEC.md)
   - **Bank:** Manages customer accounts and escrow transactions (with its own key pair). Before any transaction, the Bank registers itself under the name `"bank"` in the Identity Registry. (See /infrastructure/bank/SPEC.md)

2. **Common Components:**
   - **Microledger Module:** Provides an immutable, verifiable, append-only log using cryptographic block chaining. This module defines the canonical structure and behavior for microledgers, including block chaining, digital fingerprints, signature verification, and extended verification rules. (See /common/microledger/SPEC.md)

3. **Demo Applications:**
   - **Sleeper (Node.js):** A payee service that offers a sleep/delay API. It charges the caller $1 per second of delay and processes payment headers and microledger operations. (See /demo/sleeper/SPEC.md)
   - **Greeter (Golang):** A payee service that provides greeting and farewell endpoints, charging $1 per character of the provided name. (See /demo/greeter/SPEC.md)
   - **Agent (Python):** A payer application that leverages LangChain and OpenAI to parse a single instruction string into actionable steps. It dynamically invokes the Sleeper or Greeter endpoints as required, ensuring escrow account initialization and the correct payment header handling. (See /demo/agent/SPEC.md)

## Non Functional Requirements

As this is a demo application, every component may use in-memory databases and data structures rather than production-ready persistence.

## References

For detailed specifications and implementation guidance, please refer to the following files:
- /infrastructure/identity-registry/SPEC.md
- /infrastructure/bank/SPEC.md
- /common/microledger/SPEC.md
- /demo/sleeper/SPEC.md
- /demo/greeter/SPEC.md
- /demo/agent/SPEC.md
- /docs/Project-Overview.md