package com.training.plan.reposoty.Impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.training.plan.entity.table.ChapterTable;
import com.training.plan.reposoty.LessonCache;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class LessonCacheImpl implements LessonCache {
    private final RedisTemplate<String, Object> redisTemplate;

    private final static String ACCESS_TOKEN_KEY = "Lesson-Chapter-List";
    @Override
    public void saveChapter(String key, List<ChapterTable> list) {
        String jsonString = JSONArray.toJSONString(list, SerializerFeature.WriteMapNullValue);
        redisTemplate.opsForHash().put(ACCESS_TOKEN_KEY,key,jsonString);
    }

    @Override
    public Object getChapter(String key) {
        String result = (String) redisTemplate.opsForHash().get(ACCESS_TOKEN_KEY,key);
        return result;
    }

    @Override
    public void deleteChapter(String key) {
        redisTemplate.opsForHash().delete(ACCESS_TOKEN_KEY,key);
    }
}
