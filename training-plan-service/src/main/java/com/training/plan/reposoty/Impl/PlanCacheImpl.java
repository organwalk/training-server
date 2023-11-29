package com.training.plan.reposoty.Impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.training.plan.entity.respond.StudentInfo;
import com.training.plan.entity.respond.TeacherInfo;
import com.training.plan.entity.result.TrainPlanInfo;
import com.training.plan.entity.table.TrainingPlanTable;
import com.training.plan.reposoty.PlanCache;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
@AllArgsConstructor
public class PlanCacheImpl implements PlanCache {
    private final RedisTemplate<String, Object> redisTemplate;
    private final static String PLAN_TEACHER_HASH_KEY = "Plan-Teacher-List";

    private final static String PLAN_STUDENT_HASH_KEY = "Plan-Student-List";
    @Override
    public void saveTea(String key, List<TeacherInfo> infoList) {
        String jsonString = JSONArray.toJSONString(infoList, SerializerFeature.WriteMapNullValue);
        redisTemplate.opsForHash().put(PLAN_TEACHER_HASH_KEY,key,jsonString);
    }

    @Override
    public void saveStu(String key, List<StudentInfo> infoList) {
        String jsonString = JSONArray.toJSONString(infoList, SerializerFeature.WriteMapNullValue);
        redisTemplate.opsForHash().put(PLAN_STUDENT_HASH_KEY,key,jsonString);
    }

    @Override
    public Object getStuList(String key) {
        return redisTemplate.opsForHash().get(PLAN_STUDENT_HASH_KEY,key);
    }

    @Override
    public Object getTeaList(String key) {
        return redisTemplate.opsForHash().get(PLAN_TEACHER_HASH_KEY,key);
    }

    @Override
    public Map<Object, Object> getStuAll() {
        return redisTemplate.opsForHash().entries(PLAN_STUDENT_HASH_KEY);
    }

    @Override
    public Map<Object, Object> getTeaAll() {
        return redisTemplate.opsForHash().entries(PLAN_TEACHER_HASH_KEY);
    }

    @Override
    public void DeleteStu(Object key) {
        redisTemplate.opsForHash().delete(PLAN_STUDENT_HASH_KEY,key);
    }

    @Override
    public void DeleteTea(Object key) {
        redisTemplate.opsForHash().delete(PLAN_TEACHER_HASH_KEY,key);
    }

    @Override
    public void deleteStudentByPlanId(int planId) {
        Set<Object> keys = redisTemplate.opsForHash().keys(PLAN_STUDENT_HASH_KEY);
        for (Object key : keys) {
            if (String.valueOf(key).startsWith(planId + "-")) {
                redisTemplate.opsForHash().delete(PLAN_STUDENT_HASH_KEY, planId);
            }
        }
    }

    @Override
    public void deleteTeacherByPlanId(int planId) {
        Set<Object> keys = redisTemplate.opsForHash().keys(PLAN_TEACHER_HASH_KEY);
        for (Object key : keys) {
            if (String.valueOf(key).startsWith(planId + "-")) {
                redisTemplate.opsForHash().delete(PLAN_TEACHER_HASH_KEY, planId);
            }
        }
    }

}
