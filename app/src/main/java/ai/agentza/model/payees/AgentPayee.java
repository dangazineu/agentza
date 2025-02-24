package ai.agentza.model.payees;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.util.Optional;

@Entity
@DiscriminatorValue("AGENT")
public class AgentPayee extends Payee {

    private String agentId;
    private String walletId;

    protected AgentPayee() {}

    public AgentPayee(String payerAgentId, String agentId) {
        super(payerAgentId);
        this.agentId = agentId;
    }

    public AgentPayee(String payerAgentId, String agentId, String walletId) {
        this(payerAgentId, agentId);
        this.walletId = walletId;
    }

    public String getAgentId() {
        return this.agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getWalletId() { return walletId; }
}
