# Greeter Service Specification

Greeter is a Golang REST application offering greeting services as a payee. It provides two endpoints: one for greeting ("Hello") and one for farewell ("Goodbye"). Fees are based on the length of the provided name.

## Identity

- Registered name: `"greeter"`

## Endpoints

### 1. Hello

- **HTTP Method & URL:**
  ```
  GET /hello?name={name}
  ```

- **Query Parameter:**
  - `name` (required): The name or text to greet.

## Behavior

- Validate standard payment headers and perform required microledger operations.
- Return a greeting message (e.g., “Hello, {name}!”).
- Charge $1 per character in the provided name.

## Example Response (JSON):
```json
{
  "message": "Hello, Mark!",
  "feeCharged": 4
}
```

### 2. Goodbye

- **HTTP Method & URL:**
  ```
  GET /goodbye?name={name}
  ```

- **Query Parameter:**
  - `name` (required): The name or text for the farewell message.

## Behavior

- Validate standard payment headers and perform microledger operations.
- Return a farewell message (e.g., “Goodbye, {name}!”).
- Charge $1 per character in the provided name.

## Error Handling (Both Endpoints)

- **400 Bad Request:** If the `name` parameter is missing.