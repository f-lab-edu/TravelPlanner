package com.example.travelDiary.authenticationUtils;

import com.example.travelDiary.common.auth.domain.AuthUser;
import com.example.travelDiary.common.auth.domain.PrincipalDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContext;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Collections;
import java.util.UUID;

import static com.example.travelDiary.authenticationUtils.SecurityTestUtils.createMockAuthUser;


@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockAuthUser.SecurityContextFactory.class)
public @interface WithMockAuthUser {
    String id();

    class SecurityContextFactory implements WithSecurityContextFactory<WithMockAuthUser> {
        @Override
        public SecurityContext createSecurityContext(WithMockAuthUser withMockAuthUser) {
            AuthUser authUser = createMockAuthUser(withMockAuthUser.id());

            PrincipalDetails principalDetails = new PrincipalDetails(authUser);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    principalDetails, null, Collections.emptyList()
            );
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            return context;
        }
    }
}


