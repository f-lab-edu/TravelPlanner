package com.example.travelDiary.common.auth.v2;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

public class JwtUtils {
    private static final SecretKey key = Jwts.SIG.HS512.key().build();
    private static final long EXPIRATION_TIME = 86400000; // 1 day in milliseconds

    // Generate a JWT token
    public static String generateToken(String username) {
        return Jwts.builder().subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    // Extract username from JWT token
    public static String extractUsername(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    // Validate JWT token
    public static boolean validateToken(String token) {
        final String username = extractUsername(token);
        return (username != null && !isTokenExpired(token));
    }

    // Check if the token has expired
    private static boolean isTokenExpired(String token) {
        final Date expiration = getClaimsFromToken(token).getExpiration();
        return expiration.before(new Date());
    }

    // Get claims from the token
    public static Claims getClaimsFromToken(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }
}
