package ai.agentza.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class ApiKeyAuthentication extends AbstractAuthenticationToken {
    private final ApiKeyPrincipal apiKeyPrincipal;

    public ApiKeyAuthentication(ApiKeyPrincipal apiKeyPrincipal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.apiKeyPrincipal = apiKeyPrincipal;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return apiKeyPrincipal;
    }
}