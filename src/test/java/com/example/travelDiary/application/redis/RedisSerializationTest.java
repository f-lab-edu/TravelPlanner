package com.example.travelDiary.application.redis;

import com.example.travelDiary.common.auth.domain.ApplicationPermits;
import com.example.travelDiary.common.auth.domain.AuthUser;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class RedisSerializationTest {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    public void testAuthUserSerialization() throws JsonProcessingException {
        String token = "test_token";
        AuthUser user = AuthUser.builder()
                .id(UUID.randomUUID())
                .username("john_doe")
                .saltedPassword("hashed_password")
                .email("john.doe@example.com")
                .name("John Doe")
                .applicationPermits(ApplicationPermits.USER)
                .createdAt(Instant.now())
                .provider("PROVIDER")
                .build();

        // Store in Redis
        redisTemplate.opsForValue().set(token, user);

        // Retrieve from Redis
        Object retrieved = redisTemplate.opsForValue().get(token);

        // Ensure the retrieved object is an instance of AuthUser
        assertThat(retrieved).isInstanceOf(AuthUser.class);
        AuthUser retrievedUser = (AuthUser) retrieved;
        assertThat(retrievedUser.getUsername()).isEqualTo(user.getUsername());
        assertThat(retrievedUser.getEmail()).isEqualTo(user.getEmail());
    }
}