package com.training.plan.reposoty.Impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.training.plan.entity.respond.TeacherInfo;
import com.training.plan.reposoty.PlanCache;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
@AllArgsConstructor
public class PlanCacheImpl implements PlanCache {
    private final RedisTemplate<String, Object> redisTemplate;
    private final static String ACCESS_TOKEN_KEY1 = "Plan-Teacher-List";

    private final static String ACCESS_TOKEN_KEY2 = "Plan-Student-List";
    @Override
    public void saveTea(String key, List<TeacherInfo> infoList) {
        String jsonString = JSONArray.toJSONString(infoList, SerializerFeature.WriteMapNullValue);
        redisTemplate.opsForHash().put(ACCESS_TOKEN_KEY1,key,jsonString);
    }

    @Override
    public void saveStu(String key, List<TeacherInfo> infoList) {
        String jsonString = JSONArray.toJSONString(infoList, SerializerFeature.WriteMapNullValue);
        redisTemplate.opsForHash().put(ACCESS_TOKEN_KEY2,key,jsonString);
    }

    @Override
    public Object getStuList(String key) {
        String result = (String) redisTemplate.opsForHash().get(ACCESS_TOKEN_KEY2,key);
        return  result;
    }

    @Override
    public Object getTeaList(String key) {
        String result = (String) redisTemplate.opsForHash().get(ACCESS_TOKEN_KEY1,key);
        return result;
    }

    @Override
    public Map<Object, Object> getStuAll() {
        return redisTemplate.opsForHash().entries(ACCESS_TOKEN_KEY2);
    }

    @Override
    public Map<Object, Object> getTeaAll() {
        return redisTemplate.opsForHash().entries(ACCESS_TOKEN_KEY1);
    }

    @Override
    public void DeleteStu(Object key) {
        redisTemplate.opsForHash().delete(ACCESS_TOKEN_KEY2,key);
    }

    @Override
    public void DeleteTea(Object key) {
        redisTemplate.opsForHash().delete(ACCESS_TOKEN_KEY1,key);
    }

}
