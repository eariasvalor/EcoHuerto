package com.huerto.api.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private static final String ROLE_ADMIN = "ADMIN";
    private static final String ROLE_CUSTOMER = "CUSTOMER";
    private static final String ROUTE_PRODUCTS_ID = "/api/v1/products/{id}";

    @Value("${app.swagger.enabled:false}")
    private boolean swaggerEnabled;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/swagger-ui/index.html",
                                "/v3/api-docs",
                                "/v3/api-docs/**",
                                "/v3/api-docs.yaml",
                                "/webjars/**"
                        ).permitAll()

                        // Health
                        .requestMatchers(HttpMethod.GET, "/api/v1/health").permitAll()

                        // Auth
                        .requestMatchers("/api/v1/auth/**").permitAll()

                        // Catálogo público
                        .requestMatchers(HttpMethod.GET, "/api/v1/products").permitAll()
                        .requestMatchers(HttpMethod.GET, ROUTE_PRODUCTS_ID).permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/varieties").permitAll()

                        // Customers
                        .requestMatchers(HttpMethod.GET, "/api/v1/customers").hasRole(ROLE_ADMIN)
                        .requestMatchers(HttpMethod.GET, "/api/v1/customers/{id}").hasAnyRole(ROLE_CUSTOMER, ROLE_ADMIN)
                        .requestMatchers(HttpMethod.PUT, "/api/v1/customers/{id}").hasAnyRole(ROLE_CUSTOMER, ROLE_ADMIN)

                        // Orders
                        .requestMatchers(HttpMethod.POST,  "/api/v1/orders").hasAnyRole(ROLE_CUSTOMER, ROLE_ADMIN)
                        .requestMatchers(HttpMethod.GET,   "/api/v1/orders").hasRole(ROLE_ADMIN)
                        .requestMatchers(HttpMethod.GET,   "/api/v1/orders/{id}").hasAnyRole(ROLE_CUSTOMER, ROLE_ADMIN)
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/orders/{id}/confirm").hasRole(ROLE_ADMIN)
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/orders/{id}/preparation").hasRole(ROLE_ADMIN)
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/orders/{id}/ready").hasRole(ROLE_ADMIN)
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/orders/{id}/cancel").hasAnyRole(ROLE_CUSTOMER, ROLE_ADMIN)

                        // Products admin
                        .requestMatchers(HttpMethod.POST,   "/api/v1/products").hasRole(ROLE_ADMIN)
                        .requestMatchers(HttpMethod.PUT, ROUTE_PRODUCTS_ID).hasRole(ROLE_ADMIN)
                        .requestMatchers(HttpMethod.PATCH,  "/api/v1/products/{id}/stock").hasRole(ROLE_ADMIN)
                        .requestMatchers(HttpMethod.PATCH,  "/api/v1/products/{id}/availability").hasRole(ROLE_ADMIN)
                        .requestMatchers(HttpMethod.DELETE, ROUTE_PRODUCTS_ID).hasRole(ROLE_ADMIN)

                        // Varieties admin
                        .requestMatchers(HttpMethod.POST,   "/api/v1/varieties").hasRole(ROLE_ADMIN)
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/varieties/{id}").hasRole(ROLE_ADMIN)

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }

    @Bean
    public org.springframework.security.core.userdetails.UserDetailsService userDetailsService() {
        return username -> {
            throw new org.springframework.security.core.userdetails.UsernameNotFoundException(
                    "No UserDetailsService configured — using JWT authentication");
        };
    }
}