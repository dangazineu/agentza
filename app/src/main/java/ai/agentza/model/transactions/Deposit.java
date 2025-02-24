package ai.agentza.model.transactions;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("DEPOSIT")
public class Deposit extends Transaction {
    private String payerDescription;
    protected Deposit() {}
    public Deposit(LocalDateTime timestamp, String walletId, Double amount, String currency, TransactionStatus status, String payerDescription) {
        super(timestamp, walletId, amount, currency, status);
        this.payerDescription = payerDescription;
    }

    public String getPayerDescription() {
        return this.payerDescription;
    }
}
