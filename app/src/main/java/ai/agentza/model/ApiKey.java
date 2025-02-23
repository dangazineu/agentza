package ai.agentza.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class ApiKey {

    @Id
    private String apiKey;
    private String agentId;

    protected ApiKey() {}
    public ApiKey(String apiKey, String agentId) {
        this.apiKey = apiKey;
        this.agentId = agentId;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getAgentId() {
        return agentId;
    }

    @Override public int hashCode() { return apiKey.hashCode(); }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ApiKey apiKey = (ApiKey) obj;
        return this.apiKey.equals(apiKey.apiKey);
    }
}
