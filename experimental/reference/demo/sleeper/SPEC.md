# Sleeper Service Specification

Sleeper is a Node.js REST application that offers a sleep/delay service, functioning as a payee which requires payment for its operations.

## Identity

- Registered name: `"sleeper"`

## Endpoint: Sleep

- **HTTP Method & URL:**
  ```
  GET /sleep?time={seconds}
  ```

- **Query Parameter:**
  - `time` (required): The number of seconds to delay the response.

## Behavior

- Apply the standard payee processing behavior:
  - Validate payment-related HTTP headers:
    - `X-Proof-of-Funds`
    - `X-Allowance`
  - Perform microledger operations as defined in the common microledger module.
- Delay the response by the number of seconds specified in the `time` parameter.
- Charge the caller $1 per second of delay.
- Return a response containing the sleep duration and fee deducted.

## Response Example (JSON):
```json
{
  "message": "Slept for 5 seconds",
  "feeCharged": 5
}
```

## Error Handling

- **400 Bad Request:** If the `time` parameter is missing or if its value is not numeric.