package com.example.travelDiary.common.auth.v2;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final String frontEndUrl = "http://localhost:5173";
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        String username = authentication.getName();
        String token = JwtUtils.generateToken(username);
        log.info("token : {}", token);
        log.info("username : {}", username);
        log.info("authentication : {}", authentication);

        String redirectUrl = String.format("http://localhost:5173/authSuccess?token=%s", token);
        // Set the token in the response header
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}