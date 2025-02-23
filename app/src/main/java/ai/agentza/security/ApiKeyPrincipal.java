package ai.agentza.security;

import org.springframework.security.core.AuthenticatedPrincipal;

public record ApiKeyPrincipal (String apiKey, String owner) implements AuthenticatedPrincipal {
    @Override
    public String getName() {
        return owner;
    }
}
