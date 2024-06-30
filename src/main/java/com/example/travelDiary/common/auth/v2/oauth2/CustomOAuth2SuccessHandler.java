package com.example.travelDiary.common.auth.v2.oauth2;

import com.example.travelDiary.common.auth.domain.PrincipalDetails;
import com.example.travelDiary.common.auth.dto.JwtToken;
import com.example.travelDiary.common.auth.v2.jwt.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private String frontEndUrl = "http://localhost:5173";
    private final JwtProvider jwtProvider;


    @Autowired
    public CustomOAuth2SuccessHandler(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    public void setFrontEndUrl(@Value("${frontend.uri}")String frontEndUrl) {
        this.frontEndUrl = frontEndUrl;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        // openId connect -> authentication oauth2? from authorization code + signal openid connect -> id token + user info -> user endpoint can receive more info.
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        JwtToken token = jwtProvider.generateToken(principalDetails);
        log.info("token : {}", token);
        log.info("principalDetails : {}", principalDetails);
        log.info("authentication : {}", authentication);

        String redirectUrl = String.format(
                "http://localhost:5173/authSuccess?accessToken=%s&refreshToken=%s",
                token.getAccessToken(),
                token.getRefreshToken()
        );
        // Set the token in the response header
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}