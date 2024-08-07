package com.roamgram.travelDiary;

import com.roamgram.travelDiary.authenticationUtils.WithMockAuthUser;
import com.roamgram.travelDiary.common.auth.domain.AuthUser;
import com.roamgram.travelDiary.common.auth.domain.PrincipalDetails;
import com.roamgram.travelDiary.common.auth.service.AuthUserService;
import com.roamgram.travelDiary.common.auth.service.AuthUserServiceImpl;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.Collections;

import static com.roamgram.travelDiary.authenticationUtils.SecurityTestUtils.createMockAuthUser;

@TestConfiguration
public class TestConfig {

    @Bean
    @Primary
    public AuthUserService authUserService() {
        return Mockito.mock(AuthUserServiceImpl.class);
    }

    @Bean
    public WithSecurityContextFactory<WithMockAuthUser> securityContextFactory() {
        return new WithMockAuthUserSecurityContextFactory();
    }

    @Bean
    public TestRestTemplate testRestTemplate() {
        return new TestRestTemplate();
    }

    static class WithMockAuthUserSecurityContextFactory implements WithSecurityContextFactory<WithMockAuthUser> {
        @Override
        public SecurityContext createSecurityContext(WithMockAuthUser withMockAuthUser) {
            AuthUser authUser = createMockAuthUser(withMockAuthUser.id());
            PrincipalDetails principalDetails = new PrincipalDetails(authUser);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(principalDetails, null, Collections.emptyList());
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            return context;
        }
    }
}
