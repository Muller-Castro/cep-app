package com.muller.cepapp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.muller.cepapp.security.UserSecurityDetails;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        final String userRole  = "USER";
        final String adminRole = "ADMIN";
        
        AuthorizationManager<RequestAuthorizationContext> userOrAdmin = (authentication, context) -> {
            if (authentication == null || context == null) {
                return new AuthorizationDecision(false);
            }

            Authentication auth = authentication.get();
            if (auth == null) {
                return new AuthorizationDecision(false);
            }
            
            Object principal = auth.getPrincipal();

            if (principal instanceof UserSecurityDetails) {
                UserSecurityDetails userSecurityDetails = (UserSecurityDetails) principal;
                Long userId = userSecurityDetails.getId();

                boolean isAuthorized = false;

                String idStr = context.getVariables().get("id");
                try {
                    Long pathId = Long.parseLong(idStr);
                    
                    isAuthorized = userId.equals(pathId);
                } catch (NumberFormatException e) {
                    isAuthorized = false;
                }

                isAuthorized |= auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

                return new AuthorizationDecision(isAuthorized);
            }
            return new AuthorizationDecision(false);
        };

        http
            .csrf().disable()
            .cors().and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeHttpRequests()
                .requestMatchers("/users/login", "/users/register").permitAll()
                .requestMatchers(HttpMethod.GET, "/users").hasRole(adminRole)
                .requestMatchers(HttpMethod.GET, "/users/{id}").access(userOrAdmin)
                .requestMatchers(HttpMethod.POST, "/users").hasRole(adminRole)
                .requestMatchers(HttpMethod.PUT, "/users/{id}").access(userOrAdmin)
                .requestMatchers(HttpMethod.DELETE, "/users/{id}").access(userOrAdmin)
                
                .requestMatchers(HttpMethod.GET, "/addresses").hasRole(adminRole)
                .requestMatchers(HttpMethod.GET, "/addresses/{id}").hasAnyRole(userRole, adminRole)
                .requestMatchers(HttpMethod.POST, "/addresses").hasAnyRole(userRole, adminRole)
                .requestMatchers(HttpMethod.PUT, "/addresses/{id}").hasAnyRole(userRole, adminRole)
                .requestMatchers(HttpMethod.DELETE, "/addresses/{id}").hasRole(adminRole)
                .anyRequest().permitAll()
            .and()
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    
}
