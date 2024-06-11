package com.example.travelDiary.common.auth.controller;

import com.example.travelDiary.common.auth.domain.AuthUser;
import com.example.travelDiary.common.auth.dto.AuthRequest;
import com.example.travelDiary.common.auth.dto.JwtToken;
import com.example.travelDiary.common.auth.dto.RegistrationRequest;
import com.example.travelDiary.common.auth.service.AuthUserServiceImpl;
import com.example.travelDiary.common.auth.service.JwtAuthService;
import com.example.travelDiary.common.auth.service.TokenBlacklistService;
import com.example.travelDiary.common.auth.v2.jwt.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/authentication")
public class AppUserAuthenticationController {

    private final AuthUserServiceImpl authUserService;
    private final TokenBlacklistService tokenBlacklistService;
    private final JwtAuthService jwtAuthService;
    private final JwtProvider jwtProvider;

    @Autowired
    public AppUserAuthenticationController(AuthUserServiceImpl userService, AuthUserServiceImpl authUserService, TokenBlacklistService tokenBlacklistService, JwtAuthService jwtAuthService, JwtProvider jwtProvider) {
        this.authUserService = userService;
        this.tokenBlacklistService = tokenBlacklistService;
        this.jwtAuthService = jwtAuthService;
        this.jwtProvider = jwtProvider;
    }

    @PostMapping("/sign-in")
    public ResponseEntity<JwtToken> login(@RequestBody AuthRequest authRequest) {
        JwtToken jwtToken = jwtAuthService.signIn(authRequest.getUsername(), authRequest.getPassword());
        return ResponseEntity.ok(jwtToken);
    }

    @PostMapping("/sign-up")
    public ResponseEntity<String> signup(@RequestBody RegistrationRequest registrationRequest) {
        authUserService.register(registrationRequest);
        return ResponseEntity.ok("success");
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
        String cleanedToken = token.replace("Bearer ", "");
        authUserService.logout(cleanedToken);
        return ResponseEntity.ok("Logged out successfully");
    }
}
