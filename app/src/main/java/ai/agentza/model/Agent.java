package ai.agentza.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Agent {

    @Id
    private String agentId;
    private String name;
    private String defaultWalletId;

    protected Agent() {}
    public Agent(String agentId, String name, String defaultWalletId) {
        this.agentId = agentId;
        this.name = name;
        this.defaultWalletId = defaultWalletId;
    }

    public String getAgentId() {
        return agentId;
    }

    public String getName() {
        return name;
    }

    public String getDefaultWalletId() {
        return defaultWalletId;
    }

    public void setDefaultWalletId(String defaultWalletId) {
        this.defaultWalletId = defaultWalletId;
    }

    @Override public int hashCode() { return agentId.hashCode(); }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Agent agent = (Agent) obj;
        return agentId.equals(agent.agentId);
    }
}
