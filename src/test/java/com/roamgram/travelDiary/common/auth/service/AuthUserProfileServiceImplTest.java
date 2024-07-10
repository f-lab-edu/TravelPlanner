package com.roamgram.travelDiary.common.auth.service;

import com.roamgram.travelDiary.common.auth.domain.AuthUser;
import com.roamgram.travelDiary.common.auth.domain.PrincipalDetails;
import com.roamgram.travelDiary.common.auth.repository.AuthUserRepository;
import com.roamgram.travelDiary.common.auth.v2.jwt.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthUserProfileServiceImplTest {

    @Mock
    private AuthUserRepository authUserRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenBlacklistService tokenBlacklistService;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthUserServiceImpl authUserService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testGetCurrentAuthenticatedUser_Authenticated() {
        AuthUser mockUser = new AuthUser();
        mockUser.setId(UUID.randomUUID());
        PrincipalDetails principalDetails = new PrincipalDetails(mockUser);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(principalDetails);
        when(authUserRepository.findById(mockUser.getId())).thenReturn(Optional.of(mockUser));

        AuthUser result = authUserService.getCurrentAuthenticatedUser();
        assertNotNull(result);
        assertEquals(mockUser.getId(), result.getId());
    }

    @Test
    void testGetCurrentAuthenticatedUser_NotAuthenticated() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);

        AuthUser result = authUserService.getCurrentAuthenticatedUser();
        assertNull(result);
    }

    @Test
    void testGetCurrentAuthenticatedUser_PrincipalNotPrincipalDetails() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(new Object());

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            authUserService.getCurrentAuthenticatedUser();
        });
        assertEquals("Principal is not of type PrincipalDetails", exception.getMessage());
    }

    @Test
    void testGetCurrentAuthenticatedUser_UserNotFound() {
        AuthUser mockUser = new AuthUser();
        mockUser.setId(UUID.randomUUID());
        PrincipalDetails principalDetails = new PrincipalDetails(mockUser);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(principalDetails);
        when(authUserRepository.findById(mockUser.getId())).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            authUserService.getCurrentAuthenticatedUser();
        });
        assertEquals("Authenticated user not found", exception.getMessage());
    }
}
