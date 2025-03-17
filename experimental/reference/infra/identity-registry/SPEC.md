# Identity Registry Specification

This service maintains an in-memory mapping from application names to their public keys along with their registration timestamps. It follows a trust-on-first-use pattern.

## Endpoints

### 1. Register Identity

- **HTTP Method & URL:**
  ```
  POST /identities/{ID}
  ```
  where `{ID}` is the unique name of the application.

- **Request Headers:**
  - `X-Signature`: A cryptographic signature over the `timestamp`, generated with the application’s private key.

- **Request Body (JSON):**
  ```json
  {
    "timestamp": "ISO-8601 timestamp (must be within the past minute)",
    "publicKey": "PEM representation of the public key"
  }
  ```

- **Behavior:**
  - Validate that the provided timestamp is recent (no older than one minute).
  - Verify the `X-Signature` header by checking the signature against the timestamp using the supplied public key.
  - Store or update the mapping between `{ID}`, the public key, and the registration timestamp.
  - On success, return HTTP 201 Created.

- **Error Responses:**
  - **400 Bad Request:** If the timestamp is invalid or improperly formatted.
  - **401 Unauthorized:** If signature verification fails.

### 2. Get Identity

- **HTTP Method & URL:**
  ```
  GET /identities/{ID}
  ```

- **Response (JSON):**
  ```json
  {
    "publicKey": "PEM representation of the public key",
    "registeredAt": "ISO-8601 timestamp"
  }
  ```

- **Behavior:**
  - Retrieve and return the public key and registration timestamp corresponding to `{ID}`.
  - Return 404 Not Found if no identity exists for `{ID}`.

## Port

- The Identity Registry listens on port **8080**.