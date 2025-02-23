Requirements
- Maven (https://maven.apache.org/install.html)
- Java 21 (https://adoptium.net/temurin/releases)

# How to run
```shell
mvn spring-boot:run
```

# Browsing and Interacting with the API

OpenAPI service spec can be found at `http://localhost:8080/api/docs`
```shell
# in a system with jq installed
curl http://localhost:8080/api/docs | jq 
```

Swagger UI is also available at`http://localhost:8080/api/browser`


# How to use the API from the command line

The application comes with a series of agents pre-registered. Each agent has a pre-configured API Key `agent-${AGENT}-key`. 
i.e: The API key for agent `foo` is `agent-foo-key`.

Lists all payees registered for the agent `foo`
```shell
curl -s -X 'GET' \
  'http://localhost:8080/api/v1/payees/' \
  -H 'X-API-KEY: agent-foo-key' \
  | jq
```
Example output:
```json
[
  {
    "type": "AGENT",
    "payeeId": "a26fe706-fe3e-4bff-9c4c-fdf1923268a6",
    "payerAgentId": "foo",
    "agentId": "bar"
  },
  {
    "type": "US_ACH",
    "payeeId": "2cf9362b-0388-4d2b-b832-9293fb1aab1f",
    "payerAgentId": "foo",
    "routingNbr": "12345",
    "accountNbr": "54321"
  }
]
```

Create a new Agent-to-Agent payee for the agent `foo`
```shell
curl -s -X 'POST' \
  'http://localhost:8080/api/v1/payees/' \
  -H 'X-API-KEY: agent-foo-key' \
  -H 'Content-Type: application/json' \
  -d '{"type": "AGENT", "agentId": "qux"}' \
  | jq
```
Example output:
```json
{
  "type": "AGENT",
  "payeeId": "a26fe706-fe3e-4bff-9c4c-fdf1923268a6",
  "payerAgentId": "foo",
  "agentId": "qux"
}
```
Now running the previous command will include an additional entry in the list.
