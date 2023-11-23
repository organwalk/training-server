package com.training.resource.repository;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@AllArgsConstructor
public class ResourceLessonCacheImpl implements ResourceLessonCache{
    private final RedisTemplate<String, Object> redisTemplate;
    private final static String KEY = "Lesson-Resource-Type-FilePath";

    @Override
    public void saveResourceLessonTypeAndPath(Integer resourceId, String fileExtension, String filePath) {
        redisTemplate.opsForHash().put(KEY, String.valueOf(resourceId), fileExtension + "---" + filePath);
        redisTemplate.expire(KEY, 3, TimeUnit.DAYS);
    }

    @Override
    public String getResourceLessonTypeAndPath(Integer resourceId) {
        Object value = redisTemplate.opsForHash().get(KEY, String.valueOf(resourceId));
        return value != null ? (String) value : "";
    }

    @Override
    public void deleteResourceLessonTypeAndPath(Integer resourceId) {
        redisTemplate.opsForHash().delete(KEY, String.valueOf(resourceId));
    }
}
