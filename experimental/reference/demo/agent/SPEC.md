# Agent Specification

The Agent is implemented as a Python script that leverages LangChain and OpenAI. It acts as a payer and dynamically orchestrates calls to the Sleeper or Greeter services based on parsed instructions.

## Identity

- Registered name: `"agent"`

## Behavior

### Input

- A single instruction string, for example:
  "say hi to Mark, wait for 5 seconds, then say goodbye."

### Processing Steps

1. **Parsing:**
   - Parse the instruction into actionable steps.
2. **Dynamic Service Invocation:**
   - If a greeting is required (keywords such as "hi" or "hello"), invoke Greeter’s `/hello` endpoint.
   - For farewells, invoke Greeter’s `/goodbye` endpoint.
   - For pauses, invoke Sleeper’s `/sleep` endpoint.
3. **Escrow Account Initialization:**
   - Ensure an escrow account is created before invoking any external service if necessary.
4. **Payment Headers:**
   - Each API call must include the mandatory payment headers (`X-Proof-of-Funds` and `X-Allowance`) as specified in the common microledger guidelines.
5. **Logging:**
   - The Agent prints detailed logs or returns a JSON object outlining each executed step along with the fee charged.

### Output

- A detailed log or JSON object that summarizes all steps performed and the fee charged for each transaction.