# Project Overview

This project demonstrates how microledgers can facilitate peer-to-peer payments through a series of interconnected applications. The repository is now organized into high-level domains to clearly separate infrastructure, domain/common services, and demo applications.

## Non Functional Requirements

This is a demo application. Every component may use in-memory databases and data structures instead of production-ready persistence.

## Applications Overview

The applications are now split into the following directories:

- **Infrastructure Applications** (implemented in Java with Spring Boot, organized as modules in a Maven multi-module project):
  - **Identity Registry:** Maintains an in-memory mapping from application names to their public keys and registration time using a "trust-on-first-use" model.
  - **Bank:** Manages customer accounts and escrow transactions. It uses its own key pair, registers itself with the Identity Registry under the name `"bank"`, and provides endpoints for creating accounts and managing escrow transfers.

- **Common Components:**
  - **Microledger Module:** Provides a reference implementation of an immutable, append-only log (microledger) based on cryptographic block chaining. This module includes detailed specifications and a Java reference implementation that all service implementations must follow.

- **Demo Applications:** Implemented in different languages and demonstrating payer/payee interactions.
  - **Sleeper (Node.js):** A payee application offering sleep/delay services that charge fees proportional to the delay.
  - **Greeter (Golang):** A payee application providing greeting and farewell endpoints; fees are based on the length of the provided name.
  - **Agent (Python):** A payer application using LangChain and OpenAI to parse a single instruction string into actionable steps, dynamically invoking either Sleeper or Greeter endpoints as needed.

Refer to the respective specification files in the subdirectories for detailed endpoint definitions and behavior.