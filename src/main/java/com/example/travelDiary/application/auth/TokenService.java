package com.example.travelDiary.application.auth;

import com.example.travelDiary.application.auth.dto.Token;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ClaimsBuilder;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

@Service
public class TokenService{
    @Value("${jjwt.key}")
    private String secretKey;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }


    public Token generateToken(String uid, String role) {
        long tokenPeriod = 1000L * 60L * 10L;
        long refreshPeriod = 1000L * 60L * 60L * 24L * 30L * 3L;

        ClaimsBuilder claims = Jwts.claims().subject(uid).add("role", role);
        claims.build();

        Date now = new Date();
        return new Token(
                "accesstoken",
                "refreshToken");
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return claimsJws.getPayload().getExpiration().after(Date.from(Instant.now()));
        } catch (Exception e) {
            return false;
        }
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = secretKey.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }


    public String getUid(String token) {
        return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload().getSubject();
    }
}
