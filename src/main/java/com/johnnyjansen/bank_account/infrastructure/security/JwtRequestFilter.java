package com.johnnyjansen.bank_account.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

// Defines the JwtRequestFilter class, which extends OncePerRequestFilter
public class JwtRequestFilter extends OncePerRequestFilter {

    // Properties to store instances of JwtUtil and UserDetailsService
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    // Constructor that initializes the properties with provided instances
    public JwtRequestFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    // Method called once per request to process the filter
    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // Get the "Authorization" header value from the request
        final String authorizationHeader = request.getHeader("Authorization");

        // Check if the header exists and starts with "Bearer "
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            // Extract the JWT token from the header
            final String token = authorizationHeader.substring(7);
            // Extract the username from the JWT token
            final String username = jwtUtil.extractEmail(token);

            // If the username is not null and no authentication is present in the context
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Load the user details using the username
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                // Validate the JWT token
                if (jwtUtil.validateToken(token, username)) {
                    // Create an authentication object with user information
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    // Set the authentication in the security context
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }

        // Continue the filter chain to allow the request to proceed
        chain.doFilter(request, response);
    }

}
