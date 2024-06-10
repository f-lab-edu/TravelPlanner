package com.example.travelDiary.common.auth.controller;

import com.example.travelDiary.common.auth.domain.AuthUser;
import com.example.travelDiary.common.auth.dto.AuthRequest;
import com.example.travelDiary.common.auth.dto.JwtToken;
import com.example.travelDiary.common.auth.service.AuthUserServiceImpl;
import com.example.travelDiary.common.auth.service.JwtAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/authentication")
public class AppUserAuthenticationController {

    private final AuthUserServiceImpl authUserService;
    private final JwtAuthService jwtAuthService;

    @Autowired
    public AppUserAuthenticationController(AuthUserServiceImpl userService, AuthUserServiceImpl authUserService, JwtAuthService jwtAuthService) {
        this.authUserService = userService;
        this.jwtAuthService = jwtAuthService;
    }

    @PostMapping("/sign-in")
    public ResponseEntity<JwtToken> login(@RequestBody AuthRequest authRequest) {
        JwtToken jwtToken = jwtAuthService.signIn(authRequest.getUsername(), authRequest.getPassword());
        return ResponseEntity.ok(jwtToken);
    }

    @PostMapping("/signup")
    public AuthUser signup(@RequestBody AuthRequest authRequest) {
        return authUserService.register(authRequest.getUsername(), authRequest.getPassword());
    }
}
