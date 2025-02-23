package ai.agentza.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, ApiKeyAuthenticationFilter filter) throws Exception {
      http
              .csrf(AbstractHttpConfigurer::disable)
              .authorizeHttpRequests(
                  authRegistry -> authRegistry
                          .requestMatchers( "/error",  "/api/docs", "/api/browser", "/api/swagger-ui/**", "/api/docs/swagger-config", "/api/swagger-resources/**").permitAll()
                          .requestMatchers("/api/v1/**").authenticated()
              )
          .sessionManagement(sessionConf -> sessionConf.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
          .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}