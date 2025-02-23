package ai.agentza.security;

import ai.agentza.model.ApiKey;
import ai.agentza.model.ApiKeyRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class ApiKeyAuthenticationService {

    private static final String AUTH_TOKEN_HEADER_NAME = "X-API-KEY";

    private final ApiKeyRepository apiKeyRepository;

    public ApiKeyAuthenticationService(ApiKeyRepository apiKeyRepository) {
        this.apiKeyRepository = apiKeyRepository;
    }

    public Authentication getApiKeyAuthentication(HttpServletRequest request) {
        String apiKey = request.getHeader(AUTH_TOKEN_HEADER_NAME);
        if (apiKey != null) {
            Optional<ApiKey> apiKeyOptional = apiKeyRepository.findById(apiKey);
            if (apiKeyOptional.isPresent()) {
                return new ApiKeyAuthentication(new ApiKeyPrincipal(apiKey, apiKeyOptional.get().getAgentId()), AuthorityUtils.NO_AUTHORITIES);
            }
        }
        throw new BadCredentialsException("Invalid API Key");
    }
}