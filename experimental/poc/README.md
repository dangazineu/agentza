# Microledger Proof-of-Concept
This folder contains a Java application that implements the basic functionality of a microledger, and a Golang CLI that is capable of interacting with the microledger.

The final use of this technology should look nothing like this, but the functionality implemented here serves to visualize the concept. 

## Running
On the `microledger` folder, run `mvn spring-boot:run`. Then on another terminal window, navigate to the `cli` folder and run the following:

```shell
go run main.go create
```
This will create a new microledger in the Java application's memory. It should print a JSON object with the ledger id. Then to add blocks to the ledger, run:
```shell
go run main.go add-block  --ledger-id  "caf472de-76fb-450a-9003-6797a5cc2b68" 
```
Replace the code above with the ledger ID that was printed in the `create` command. You can run the `add-block` command as many times as you'd like, as it appends the block to the end of the ledger.

Then, to see the result, run the following command:

```shell
go run main.go get-ledger --ledger-id  "caf472de-76fb-450a-9003-6797a5cc2b68"
```

Again, replacing the ledger ID with the one provided in the first command. This should print the entire ledger.
