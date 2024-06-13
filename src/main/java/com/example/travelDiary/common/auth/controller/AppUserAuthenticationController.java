package com.example.travelDiary.common.auth.controller;

import com.example.travelDiary.common.auth.dto.AuthRequest;
import com.example.travelDiary.common.auth.dto.JwtToken;
import com.example.travelDiary.common.auth.dto.RegistrationRequest;
import com.example.travelDiary.common.auth.service.AuthUserServiceImpl;
import com.example.travelDiary.common.auth.service.JwtAuthService;
import com.example.travelDiary.common.auth.v2.jwt.JwtProvider;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/authentication")
@Slf4j
public class AppUserAuthenticationController {

    private final AuthUserServiceImpl authUserService;
    private final JwtAuthService jwtAuthService;
    private final JwtProvider jwtProvider;

    @Autowired
    public AppUserAuthenticationController(AuthUserServiceImpl userService, JwtAuthService jwtAuthService, JwtProvider jwtProvider, JwtProvider jwtProvider1) {
        this.authUserService = userService;
        this.jwtAuthService = jwtAuthService;
        this.jwtProvider = jwtProvider1;
    }

    @Tag(name = "public")
    @PostMapping("/sign-in")
    public ResponseEntity<JwtToken> login(@RequestBody AuthRequest authRequest) {
        JwtToken jwtToken = jwtAuthService.signIn(authRequest.getUsername(), authRequest.getPassword());
        return ResponseEntity.ok(jwtToken);
    }

    @Tag(name = "public")
    @PostMapping("/sign-up")
    public ResponseEntity<String> signup(@RequestBody RegistrationRequest registrationRequest) {
        log.info("register user : {}", registrationRequest.getUsername());
        authUserService.register(registrationRequest);
        return ResponseEntity.ok("success");
    }

    @Tag(name = "secure")
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
        String cleanedToken = token.replace("Bearer ", "");
        authUserService.logout(cleanedToken);
        return ResponseEntity.ok("Logged out successfully");
    }

    @Tag(name = "secure")
    @PostMapping("/refresh")
    public ResponseEntity<String> refresh(@RequestBody String refreshToken) {
        // Verify the refresh token and generate a new access token

        if (jwtProvider.validateToken(refreshToken)) {
            String newAccessToken = jwtAuthService.refresh(refreshToken);
            return ResponseEntity.ok(newAccessToken);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
