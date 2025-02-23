package ai.agentza.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@Component
public class ApiKeyAuthenticationFilter extends GenericFilterBean {

    private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(ApiKeyAuthenticationFilter.class);

    private final ApiKeyAuthenticationService apiKeyAuthenticationService;

    public ApiKeyAuthenticationFilter(ApiKeyAuthenticationService apiKeyAuthenticationService) {
        this.apiKeyAuthenticationService = apiKeyAuthenticationService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
      throws IOException, ServletException {
        try {
            Authentication authentication = apiKeyAuthenticationService.getApiKeyAuthentication((HttpServletRequest) request);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception exp) {
            LOG.debug("Authentication failed: {}", exp.getMessage());
        }
        filterChain.doFilter(request, response);
    }

}