package ai.agentza.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ApiKeyAuthenticationService {

    private static final String AUTH_TOKEN_HEADER_NAME = "X-API-KEY";

    public Authentication getApiKeyAuthentication(HttpServletRequest request) {
        String apiKey = request.getHeader(AUTH_TOKEN_HEADER_NAME);
        if (apiKey == null || !apiKeys.containsKey(apiKey)) {
            throw new BadCredentialsException("Invalid API Key");
        }
        return new ApiKeyAuthentication(apiKeys.get(apiKey), AuthorityUtils.NO_AUTHORITIES);
    }

    private final Map<String, ApiKeyPrincipal> apiKeys = Stream.of(
            new ApiKeyPrincipal("agent-foo-key", "agent-foo"),
            new ApiKeyPrincipal("agent-bar-key", "agent-bar"),
            new ApiKeyPrincipal("agent-baz-key", "agent-baz"),
            new ApiKeyPrincipal("agent-qux-key", "agent-qux"),
            new ApiKeyPrincipal("agent-quux-key", "agent-quux"),
            new ApiKeyPrincipal("agent-corge-key", "agent-corge"),
            new ApiKeyPrincipal("agent-grault-key", "agent-grault"),
            new ApiKeyPrincipal("agent-garply-key", "agent-garply"),
            new ApiKeyPrincipal("agent-waldo-key", "agent-waldo"),
            new ApiKeyPrincipal("agent-fred-key", "agent-fred"),
            new ApiKeyPrincipal("agent-plugh-key", "agent-plugh"),
            new ApiKeyPrincipal("agent-xyzzy-key", "agent-xyzzy"),
            new ApiKeyPrincipal("agent-thud-key", "agent-thud")
    ).collect(Collectors.toMap(ApiKeyPrincipal::apiKey, apiKey -> apiKey));
}