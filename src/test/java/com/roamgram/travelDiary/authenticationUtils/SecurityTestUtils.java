package com.roamgram.travelDiary.authenticationUtils;

import com.roamgram.travelDiary.common.auth.domain.AuthUser;
import com.roamgram.travelDiary.common.auth.domain.PrincipalDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.UUID;

public class SecurityTestUtils {

    public static void mockAuthenticatedUser(AuthUser authUser) {
        PrincipalDetails principalDetails = new PrincipalDetails(authUser);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                principalDetails, null, Collections.emptyList()
        );
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    public static AuthUser createMockAuthUser(String UUIDString) {
        AuthUser authUser = new AuthUser();
        authUser.setId(UUID.fromString(UUIDString));
        authUser.setUsername("testUser");
        authUser.setEmail("test@test.com");
        authUser.setSaltedPassword("test");
        authUser.setName("test");
        // Set other properties as needed
        return authUser;
    }
}
