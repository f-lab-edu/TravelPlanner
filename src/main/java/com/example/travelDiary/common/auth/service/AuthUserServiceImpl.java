package com.example.travelDiary.common.auth.service;


import com.example.travelDiary.application.events.EventPublisher;
import com.example.travelDiary.application.events.authuser.UserCreationEvent;
import com.example.travelDiary.common.auth.domain.AuthUser;
import com.example.travelDiary.common.auth.domain.PrincipalDetails;
import com.example.travelDiary.common.auth.dto.RegistrationRequest;
import com.example.travelDiary.common.auth.repository.AuthUserRepository;
import com.example.travelDiary.common.auth.domain.ApplicationPermits;
import com.example.travelDiary.common.auth.v2.jwt.JwtProvider;
import com.example.travelDiary.domain.model.user.UserProfile;
import com.example.travelDiary.repository.persistence.user.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class AuthUserServiceImpl implements AuthUserService {

    private final AuthUserRepository authUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenBlacklistService tokenBlacklistService;
    private final JwtProvider jwtProvider;
    private final String PROVIDER = "APP";
    private final ApplicationPermits USER = ApplicationPermits.USER;
    private final EventPublisher eventPublisher;
    private final UserProfileRepository userProfileRepository;

    @Autowired
    public AuthUserServiceImpl(AuthUserRepository userRepository, PasswordEncoder passwordEncoder, TokenBlacklistService tokenBlacklistService, JwtProvider jwtProvider, EventPublisher eventPublisher, UserProfileRepository userProfileRepository1) {

        this.authUserRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenBlacklistService = tokenBlacklistService;
        this.jwtProvider = jwtProvider;
        this.eventPublisher = eventPublisher;
        this.userProfileRepository = userProfileRepository1;
    }

    @Override
    public AuthUser register(RegistrationRequest registrationRequest) {
        if (authUserRepository.findByUsername(registrationRequest.getUsername()).isPresent()) {
            throw new RuntimeException("User with same Username already exists");
        }

        AuthUser user = AuthUser.builder()
                .username(registrationRequest.getUsername())
                .saltedPassword(passwordEncoder.encode(registrationRequest.getPassword()))
                .email(registrationRequest.getEmail())
                .name(registrationRequest.getUsername())
                .applicationPermits(USER)
                .createdAt(Instant.now())
                .provider(PROVIDER)
                .build();

        AuthUser registeredUser = authUserRepository.save(user);
        user.setProviderId(String.valueOf(registeredUser.getId()));
        eventPublisher.publishEvent(new UserCreationEvent(user));
        return authUserRepository.save(user);
    }

    @Override
    public void logout(String token) {
        long expirationTime = jwtProvider.getExpirationTime(token);
        tokenBlacklistService.blacklistToken(token, expirationTime);
    }

    @Override
    public AuthUser getCurrentAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        if (authentication.getPrincipal() instanceof PrincipalDetails) {
            AuthUser currentUser = ((PrincipalDetails) authentication.getPrincipal()).getUser();
            AuthUser managedUser = authUserRepository.findById(currentUser.getId())
                    .orElseThrow(() -> new IllegalStateException("Authenticated user not found"));

            return managedUser;
        }
        throw new IllegalStateException("Principal is not of type PrincipalDetails");
    }

    @Override
    public UserProfile getCurrentUser() {
        AuthUser currentUser = getCurrentAuthenticatedUser();
        UserProfile userProfile = userProfileRepository.findByAuthUserId(currentUser.getId()).orElseThrow(() -> new IllegalStateException("Authenticated user not found"));
        return userProfile;
    }

    @Override
    public UserProfile toUser(AuthUser authUser) {
        UserProfile userProfile = userProfileRepository.findByAuthUserId(authUser.getId()).orElseThrow(() -> new IllegalStateException("Authenticated user not found"));
        return userProfile;
    }


}

