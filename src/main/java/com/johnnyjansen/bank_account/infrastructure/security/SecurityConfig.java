package com.johnnyjansen.bank_account.infrastructure.security;


import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@SecurityScheme(name = SecurityConfig.SECURITY_SCHEME,
                type = SecuritySchemeType.HTTP,
                bearerFormat = "JWT",
                scheme = "bearer")

public class SecurityConfig {

    public static final String SECURITY_SCHEME = "bearerAuth";

    // JwtUtil and UserDetailsService instances injected by Spring
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    // Constructor for dependency injection of JwtUtil and UserDetailsService
    @Autowired
    public SecurityConfig(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    // Security filter configuration
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        JwtRequestFilter jwtRequestFilter = new JwtRequestFilter(jwtUtil, userDetailsService);

        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        // Public endpoints (Swagger and account management)
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/userAccount").permitAll()
                        .requestMatchers(HttpMethod.POST, "/userAccount").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/user/update").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/user/delete").permitAll()

                        // Protected endpoints
                        .requestMatchers("/user/**").authenticated()
                        .anyRequest().authenticated()
                )
                // Use stateless session (no HTTP session)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // Add the JWT filter before the default Spring Security filter
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Configures AuthenticationManager using AuthenticationConfiguration
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // Password encoder bean (BCrypt)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}