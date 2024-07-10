package com.roamgram.travelDiary.common.auth;

import com.roamgram.travelDiary.common.auth.service.PrincipalService;
import com.roamgram.travelDiary.common.auth.v2.jwt.JwtProvider;
import com.roamgram.travelDiary.common.auth.v2.oauth2.CustomOAuth2SuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AuthConfiguration {

    private final PrincipalService principalService;

    public AuthConfiguration(PrincipalService principalService) {
        this.principalService = principalService;
    }


    @Bean
    public JwtProvider jwtProvider(){
        return new JwtProvider(principalService);
    }

    @Bean
    public CustomOAuth2SuccessHandler customOAuth2SuccessHandler() {
        return new CustomOAuth2SuccessHandler(jwtProvider());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
