package com.example.travelDiary.common.auth.service;

import com.example.travelDiary.common.auth.domain.AuthUser;
import com.example.travelDiary.common.auth.domain.PrincipalDetails;
import com.example.travelDiary.common.auth.dto.JwtToken;
import com.example.travelDiary.common.auth.repository.AuthUserRepository;
import com.example.travelDiary.common.auth.v2.jwt.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class JwtAuthService {
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;
    private final AuthUserRepository authUserRepository;

    @Autowired
    public JwtAuthService(JwtProvider jwtProvider, AuthenticationManager authenticationManager, AuthUserRepository authUserRepository) {
        this.jwtProvider = jwtProvider;
        this.authenticationManager = authenticationManager;
        this.authUserRepository = authUserRepository;
    }

    @Transactional
    public JwtToken signIn(String username, String password) {
        // 1. username + password 기반으로 Authentication 객체 생성
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);

        // 2. 실제 검증 (authenticate() 메서드가 실행될 때 PrincipalDetailsService 의 loadUserByUsername 실행)
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        return jwtProvider.generateToken(principalDetails);
    }

    @Transactional
    public String refresh(String refreshToken) {
        String authUserId = jwtProvider.parseClaims(refreshToken).getSubject();
        Optional<AuthUser> authUser = authUserRepository.findByProviderId(authUserId);
        AuthUser authUserEntity = authUser.orElseThrow();
        PrincipalDetails principalDetails = new PrincipalDetails(authUserEntity);
        return jwtProvider.generateAccessToken(principalDetails);
    }
}

