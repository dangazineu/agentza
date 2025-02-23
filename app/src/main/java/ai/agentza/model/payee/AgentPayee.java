package ai.agentza.model.payee;

import ai.agentza.model.Agent;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("AGENT")
public class AgentPayee extends Payee {

    private String agentId;

    protected AgentPayee() {}

    public AgentPayee(String payerAgentId, String agentId) {
        super(payerAgentId);
        this.agentId = agentId;
    }

    public String getAgentId() {
        return this.agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }
}
