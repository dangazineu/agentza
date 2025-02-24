package ai.agentza.model.transactions;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("WITHDRAWAL")
public class Withdrawal extends Transaction {
    private String payeeId;

    protected Withdrawal() {}
    public Withdrawal(LocalDateTime timestamp, String walletId, Double amount, String currency, TransactionStatus status, String payeeId) {
        super(timestamp, walletId, amount, currency, status);
        this.payeeId = payeeId;
    }

    public String getPayeeId() {
        return this.payeeId;
    }
}
