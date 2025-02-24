package ai.agentza.model.transactions;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
public abstract class Transaction {

    @Id @UuidGenerator private String transactionId;
    private LocalDateTime timestamp;
    private String walletId;
    private Double amount;
    private String currency;
    private TransactionStatus status;

    protected Transaction() {}

    public Transaction(LocalDateTime timestamp, String walletId, Double amount, String currency, TransactionStatus status) {
        this.timestamp = timestamp;
        this.walletId = walletId;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
    }

    public enum TransactionStatus {
        PENDING,
        COMPLETED,
        FAILED,
        CANCELLED
    }

    public String getTransactionId() {
        return transactionId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getWalletId() {
        return walletId;
    }

    public Double getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public TransactionStatus getStatus() {
        return status;
    }
}


