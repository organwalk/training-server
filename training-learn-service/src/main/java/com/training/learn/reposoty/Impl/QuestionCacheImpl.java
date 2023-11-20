package com.training.learn.reposoty.Impl;

import com.alibaba.fastjson.JSON;
import com.training.learn.entity.request.CacheReq;
import com.training.learn.entity.result.TeaQuestionResult;
import com.training.learn.reposoty.QuestionCache;
import lombok.AllArgsConstructor;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@AllArgsConstructor
public class QuestionCacheImpl implements QuestionCache {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     *  保存试题
     * @param test_id 试题id
     * 2023/11/16
     */
    @Override
    public void saveQuestion(int test_id, List<TeaQuestionResult.Question> question) {
        String key = " Learn-"+test_id+"-Test-Questions";
        String StringJson = JSON.toJSONString(question);
        redisTemplate.opsForValue().set(key,StringJson);
        redisTemplate.expire(key,24, TimeUnit.HOURS);
    }

    /**
     * 获取试题
     * @param test_id 试题id
     * @return 根据处理结果返回对应消息
     * 2023/11/16
     */
    @Override
    public List<TeaQuestionResult.Question> getQuestion(int test_id) {
        String key = " Learn-"+test_id+"-Test-Questions";
        String StringJson = (String) redisTemplate.opsForValue().get(key);
        return JSON.parseArray(StringJson,TeaQuestionResult.Question.class);
    }
    /**
     *  删除指定试题
     * @param test_id 试题id
     * 2023/11/15
     */
    @Override
    public void deleteQuestion(int test_id) {
        String key = " Learn-"+test_id+"-Test-Questions";
        redisTemplate.delete(key);
    }
    /**
     *  保存暂时编写的试题
     * @param test_id 试题id
     * 2023/11/15
     */
    @Override
    public void saveCache(int test_id, CacheReq req) {
        String key = " Learn-"+test_id+"-Test-Questions";
        String StringJson = JSON.toJSONString(req.getQuestions());
        redisTemplate.opsForValue().set(key,StringJson);
        redisTemplate.expire(key,7,TimeUnit.DAYS);
    }

    /**
     *  删除暂时编写的试题
     * @param test_id 试题id
     * @return 根据处理结果返回对应消息
     * 2023/11/15
     */
    @Override
    public List<CacheReq.Question> getCache(int test_id) {
        String key = " Learn-"+test_id+"-Test-Questions";
        String StringJson = (String)redisTemplate.opsForValue().get(key);
        return JSON.parseArray(StringJson,CacheReq.Question.class);
    }

    /**
     *  判断指定测试的缓存是否存在
     * @param test_id 试题id
     * @return 根据处理结果返回对应消息
     * 2023/11/15
     */
    @Override
    public boolean judgeKeyExit(int test_id) {
        String key = " Learn-"+test_id+"-Test-Questions";
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        return valueOperations.get(key) != null;
    }
}
