Requirements
- Maven (https://maven.apache.org/install.html)
- Java 21 (https://adoptium.net/temurin/releases)

How to run
```shell
mvn spring-boot:run
```

How to see the API
```shell
http://localhost:8080/api/docs
```

How to interact with the API
```shell
http://localhost:8080/api/browser
```

How to use the API from the command line
```shell
curl -X 'GET' \
'http://localhost:8080/api/v1/transactions/dskhjdfgsklgjf' \
-H 'accept: */*' \
-H 'X-API-KEY: agent-foo-key'
```