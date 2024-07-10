package com.roamgram.travelDiary.application.service.review;

import com.roamgram.travelDiary.domain.model.review.MediaFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
public class MediaFileRedisService {
    private final RedisTemplate<String, Object> redisTemplate;
    private static final long CACHE_EXPIRATION_TIME = 40; // cache expiration time in minutes

    @Autowired
    public MediaFileRedisService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void cacheMediaFile(String key, MediaFile mediaFile) {
        redisTemplate.opsForValue().set(key, mediaFile, Duration.ofMinutes(CACHE_EXPIRATION_TIME));
    }

    public MediaFile getMediaFile(String key) {
        return (MediaFile) redisTemplate.opsForValue().get(key);
    }

    public void deleteMediaFile(String key) {
        redisTemplate.delete(key);
    }
}
