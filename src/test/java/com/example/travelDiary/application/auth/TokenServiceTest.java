package com.example.travelDiary.application.auth;

import com.example.travelDiary.common.auth.TokenService;
import com.example.travelDiary.common.auth.dto.Token;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class TokenServiceTest {

    private final TokenService tokenService;

    @Autowired
    TokenServiceTest(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Test
    void generateToken() {
        Token token = tokenService.generateToken("ILIKEYOURCUTG", "USER");
        Assertions.assertNotEquals(token.getToken().length() , 0);
    }

    @Test
    void expireToken() {
        Token token = tokenService.generateToken("ILIKEYOURCUTG", "USER");
        boolean bool = tokenService.validateToken(token.getToken(), Date.from(Instant.now().plusSeconds(TokenService.tokenPeriod + 1)));
        assertFalse(bool);
    }

    @Test
    void validateToken() {
        Token token = tokenService.generateToken("ILIKEYOURCUTG", "USER");
        boolean bool = tokenService.validateToken(token.getToken(), Date.from(Instant.now()));
        assertTrue(bool);
    }
}