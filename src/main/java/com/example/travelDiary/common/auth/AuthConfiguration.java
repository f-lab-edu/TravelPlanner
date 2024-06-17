package com.example.travelDiary.common.auth;

import com.example.travelDiary.common.auth.service.PrincipalService;
import com.example.travelDiary.common.auth.service.TokenBlacklistService;
import com.example.travelDiary.common.auth.v2.jwt.JwtAuthenticationFilter;
import com.example.travelDiary.common.auth.v2.jwt.JwtProvider;
import com.example.travelDiary.common.auth.v2.oauth2.CustomOAuth2SuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
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
