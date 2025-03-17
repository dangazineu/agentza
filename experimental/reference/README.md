# Microledger Payment Reference Implementation and Demo

This repository demonstrates how microledgers can facilitate secure, peer-to-peer payments through a series of interconnected applications.

## Repository Structure

- **/infrastructure**
  Contains Java-based Spring Boot services:
  - **identity-registry:** Manages application identities.
  - **bank:** Handles customer accounts and escrow transactions.

- **/common**
  Contains common modules:
  - **microledger:** A reference implementation of an immutable, verifiable microledger.

- **/demo**
  Demonstration applications implemented across different languages:
  - **sleeper (Node.js):** Offers sleep/delay services.
  - **greeter (Golang):** Provides greeting and farewell endpoints.
  - **agent (Python):** Orchestrates service calls based on parsed natural-language instructions.

For an in-depth overview of the project and detailed specifications for each component, please see [Project Overview](/docs/Project-Overview.md).