package ai.agentza.security;

import ai.agentza.model.Agent;
import org.springframework.security.core.AuthenticatedPrincipal;

public record ApiKeyPrincipal (String apiKey, String agentId) implements AuthenticatedPrincipal {
    @Override
    public String getName() {
        return agentId;
    }
}
