package com.example.travelDiary.common.auth.v2.ratelimiter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private static final long MAX_REQUESTS = 100;
    private static final long WINDOW_SIZE = 60; // in seconds

    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public RateLimitingFilter(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain)
            throws ServletException, IOException {

        String clientIP = request.getRemoteAddr();
        String key = "rate_limit:" + clientIP;
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();

        Number currentCount = (Number) ops.get(key);
        if (currentCount == null) {
            ops.set(key, 1L, WINDOW_SIZE, TimeUnit.SECONDS);
        } else if (currentCount.longValue() < MAX_REQUESTS) {
            ops.increment(key);
        } else {
            response.setStatus(429);
            response.getWriter().write("Too many requests");
            return;
        }

        filterChain.doFilter(request, response);
    }
}