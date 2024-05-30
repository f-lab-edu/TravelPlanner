package com.example.travelDiary.application.auth;

import com.example.travelDiary.application.auth.dto.Token;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ClaimsBuilder;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

@Service
@Slf4j
public class TokenService{
    @Value("${jjwt.key}")
    private String secretKey;
    public static long tokenPeriod = 10L * 60L; // 10 minutes in seconds
    long refreshPeriod = 3L * 30L * 24L * 60L * 60L; // 3 months in seconds

    public Token generateToken(String uid, String role) {

        Instant now = Instant.now();
        Instant accessTokenExpiry = now.plusSeconds(tokenPeriod);
        Instant refreshTokenExpiry = now.plusSeconds(refreshPeriod);

        String accessToken = Jwts.builder().subject(uid)
                .claim("role", role).issuedAt(Date.from(now)).expiration(Date.from(accessTokenExpiry))
                .signWith(getSigningKey())
                .compact();

        String refreshToken = Jwts.builder().subject(uid)
                .claim("role", role).issuedAt(Date.from(now)).expiration(Date.from(refreshTokenExpiry))
                .signWith(getSigningKey())
                .compact();

        return new Token(accessToken, refreshToken);
    }

    public Token generateTestToken(String uid, String role) {

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
            log.info(claimsJws.getPayload().toString());
            log.info(claimsJws.getPayload().getExpiration().toString());
            return claimsJws.getPayload().getExpiration().after(Date.from(Instant.now()));
        } catch (Exception e) {
            return false;
        }
    }

    public boolean validateToken(String token, Date now) {
        try {
            Jws<Claims> claimsJws = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            log.info(claimsJws.getPayload().toString());
            log.info(claimsJws.getPayload().getExpiration().toString());
            return claimsJws.getPayload().getExpiration().after(now);
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
