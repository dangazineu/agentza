package ai.agentza.model;

import ai.agentza.model.payee.Payee;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.hibernate.annotations.UuidGenerator;

@Entity
public class Wallet {
    @Id private String walletId;
    private String agentId;
    private String description;
    private Double balance;

    protected Wallet() {}
    public Wallet(String walletId, String agentId, String description, Double balance) {
        this.walletId = walletId;
        this.agentId = agentId;
        this.description = description;
        this.balance = balance;
    }

    @Override public int hashCode() { return walletId.hashCode(); }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Wallet wallet = (Wallet) obj;
        return walletId.equals(wallet.walletId);
    }

    public String getAgentId() {
        return agentId;
    }

    public String getDescription() {
        return description;
    }

    public Double getBalance() {
        return balance;
    }

    public String getWalletId() {
        return walletId;
    }
}
