package com.example.travelDiary.common.auth.permissions.OPA;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Deprecated
public class OpaAuthorizationFilter extends OncePerRequestFilter {
    private final OpaService opaService;

    public OpaAuthorizationFilter(OpaService opaService) {
        this.opaService = opaService;
    }

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain)
            throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && opaService.evaluatePolicy(authentication.getName(), request.getMethod(), request.getRequestURI())) {
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }
    }
}

