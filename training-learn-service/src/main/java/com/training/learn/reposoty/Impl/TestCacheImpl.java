package com.training.learn.reposoty.Impl;

import com.training.learn.reposoty.TestCache;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class TestCacheImpl implements TestCache {
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String TEST_TIME_KEY = "Test-Time-";
    private static final String TEST_STUDENT_KEY = "Test-Student-";

    @Override
    public void cacheTestDateTime(Integer testId, String start, String end, Integer lessonId) {
        redisTemplate.opsForValue().set(TEST_TIME_KEY + testId, start + "_" + end + "__" + lessonId);
        redisTemplate.expire(TEST_TIME_KEY + testId, Duration.ofHours(2));
    }

    @Override
    public String getTestTimeCache(Integer testId) {
        Object value = redisTemplate.opsForValue().get(TEST_TIME_KEY + testId);
        return Objects.isNull(value) ? "" : (String) value;
    }

    @Override
    public void cacheTestStudent(Integer testId, List<Integer> studentIdList) {
        redisTemplate.opsForValue().set(TEST_STUDENT_KEY + testId,
                studentIdList.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(",")));
    }

    @Override
    public List<Integer> getTestStudentCache(Integer testId) {
        Object value = redisTemplate.opsForValue().get(TEST_STUDENT_KEY + testId);
        return Objects.isNull(value)
                ? new ArrayList<>()
                : Arrays.stream(String.valueOf(value).split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }
}
