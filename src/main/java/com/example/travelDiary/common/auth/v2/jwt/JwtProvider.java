package com.example.travelDiary.common.auth.v2.jwt;

import com.example.travelDiary.common.auth.domain.AuthUser;
import com.example.travelDiary.common.auth.domain.PrincipalDetails;
import com.example.travelDiary.common.auth.domain.ApplicationPermits;
import com.example.travelDiary.common.auth.dto.JwtToken;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JwtProvider {
    //change to jjwt.io key -> to maintain jwt key during multiple backend
    private static SecretKey KEY;
    private static final long ACCESS_EXPIRATION_TIME = 900000; // 15 minutes in milliseconds
    private static final long REFRESH_EXPIRATION_TIME = 604800000 ; // 1 day in milliseconds

    @Value("${jjwt.key}")
    public void setKey (String key) {
        log.info(key);
        KEY = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(key));
    }

    // Generate a JWT token
    public JwtToken generateToken(PrincipalDetails authentication) {
        // get authorities
        String authorities = getAuthorities(authentication);

        Instant now = Instant.now();

        // Create Access Token
        String accessToken = generateAccessToken(authentication);
        // Create Refresh Token
        String refreshToken = generateRefreshToken(authentication);

        return JwtToken.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public String generateAccessToken(PrincipalDetails principal) {
        Instant now = Instant.now();
        String authorities = getAuthorities(principal);
        Instant accessTokenExpiresIn = now.plusMillis(ACCESS_EXPIRATION_TIME);
        String accessToken = Jwts.builder()
                .subject(principal.getName())
                .claim("auth", authorities)
                .expiration(Date.from(accessTokenExpiresIn))
                .signWith(KEY, Jwts.SIG.HS512)
                .compact();
        return accessToken;
    }


    private String generateRefreshToken(PrincipalDetails principal) {
        Instant now = Instant.now();
        String authorities = getAuthorities(principal);
        Instant refreshTokenExpiresIn = now.plusMillis(REFRESH_EXPIRATION_TIME);
        String refreshToken = Jwts.builder()
                .subject(principal.getName())
                .claim("auth", authorities)
                .expiration(Date.from(refreshTokenExpiresIn))
                .signWith(KEY, Jwts.SIG.HS512)
                .compact();
        return refreshToken;
    }

    private @NotNull String getAuthorities(PrincipalDetails authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        return authorities;
    }


    //refresh -> server accept refresh -> validate refresh token -> generate new token
    //refresh token need to have a much longer expiration time then accesstoken. ( accesstoken 15 mins / refresh token 1week )
    // Jwt 토큰을 복호화하여 토큰에 들어있는 정보를 꺼내는 메서드
    public Authentication getAuthentication(String accessToken) {
        // Jwt 토큰 복호화
        Claims claims = parseClaims(accessToken);

        if (claims.get("auth") == null) {
            throw new RuntimeException("this token has no information about authority.");
        }

        // 클레임에서 권한 정보 가져오기
        Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get("auth").toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        // UserDetails 객체를 만들어서 Authentication return
        // UserDetails: interface, User: UserDetails를 구현한 class
        // Create PrincipalDetails object and return Authentication
        AuthUser user = new AuthUser();
        user.setUsername(claims.getSubject());
        user.setApplicationPermits(ApplicationPermits.valueOf(claims.get("auth").toString()));
        PrincipalDetails principal = new PrincipalDetails(user);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    // verify token info
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(KEY)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
        }
        return false;
    }

    // accessToken decode to get payload
    public Claims parseClaims(String accessToken) {
        try {
            return Jwts.parser()
                    .verifyWith(KEY)
                    .build()
                    .parseSignedClaims(accessToken)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public long getExpirationTime(String cleanedToken) {
        Claims claims = Jwts.parser()
                .verifyWith(KEY)
                .build()
                .parseSignedClaims(cleanedToken)
                .getPayload();
        Date expiration = claims.getExpiration();
        return expiration.getTime();
    }
}
