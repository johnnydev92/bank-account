package com.johnnyjansen.bank_account.infrastructure.security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Service
public class JwtUtil {

    // Base64-encoded secret key used for signing the JWT
    private final String base64SecretKey = "c2VjdXJlLWNoYXJhY3Rlci1zdXBlci1zZWFjdXJlLXdpdGgtYS1saW5nLXRleHQ=";

    // Generates a SecretKey from the Base64-encoded secret
    private SecretKey getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(base64SecretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Generates a JWT token using the user's email as the subject, valid for 1 hour
    public String generateToken(String email) {
        return Jwts.builder()
                .subject(email) // Set the email as the subject (identifier) of the token
                .issuedAt(new Date()) // Set the issue date (now)
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // Set expiration to 1 hour from now
                .signWith(getSigningKey()) // Sign the token with the secret key
                .compact(); // Build and return the JWT string
    }

    // Extracts claims (payload) from the JWT
    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey()) // Use the same signing key to validate the token
                .build()
                .parseSignedClaims(token) // Parse the token and verify its signature
                .getPayload(); // Return the token's payload (claims)
    }

    // Extracts the user's email (subject) from the token
    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    // Extracts the user's cpf from the token
    public String extractCpf(String token) {
        return extractClaims(token).get("cpf", String.class);
    }

    // Checks whether the token is expired
    public boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    // Validates the token by comparing the username (email) and checking expiration
    public boolean validateToken(String token, String username) {
        final String extractedUsername = extractEmail(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }
}
