package ai.agentza.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, ApiKeyAuthenticationFilter filter) throws Exception {
      http
              .csrf(AbstractHttpConfigurer::disable)
              .authorizeHttpRequests(
                  authRegistry -> authRegistry
                          .requestMatchers(  "/api/docs", "/api/browser", "/api/swagger-ui/**", "/api/docs/swagger-config", "/api/swagger-resources/**").permitAll()
                          .requestMatchers("/api/v1/**").authenticated()
              )
          .sessionManagement(sessionConf -> sessionConf.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
          .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}