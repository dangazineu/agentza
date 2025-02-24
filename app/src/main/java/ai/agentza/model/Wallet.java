package ai.agentza.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Wallet {
    @Id private String walletId;
    private String agentId;
    private String description;
    private String currency;
    private Double balance;

    protected Wallet() {}
    public Wallet(String walletId, String agentId, String description, String currency, Double balance) {
        this.walletId = walletId;
        this.agentId = agentId;
        this.description = description;
        this.currency = currency;
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

    public String getCurrency() {
        return currency;
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

    public void setBalance(double v) {
        this.balance = v;
    }
}
