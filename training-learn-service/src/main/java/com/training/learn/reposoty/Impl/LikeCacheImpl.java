package com.training.learn.reposoty.Impl;

import com.training.learn.reposoty.LikeCache;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * by zhaozhifeng 2023-11-10
 */
@Component
@AllArgsConstructor
public class LikeCacheImpl implements LikeCache {

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void saveCommentLike(String key, String field, Integer value) {
        String NewKey = "Learn-"+key+"-Comment-Like-List";
        redisTemplate.opsForHash().put(NewKey,field,value);
        long expireTime =  60*60*24; //设置过期时间为24小时
        redisTemplate.expire(NewKey,expireTime, TimeUnit.SECONDS);
    }

    @Override
    public void saveReplyLike(String key, String field, Integer value) {
        String NewKey = "Learn-"+key+"-Reply-Like-List";
        redisTemplate.opsForHash().put(NewKey,field,value);
        long expireTime =  60*60*24; //设置过期时间为24小时
        redisTemplate.expire(NewKey,expireTime, TimeUnit.SECONDS);
    }

    @Override
    public Object getCommentLike(String key, String field) {
        String NewKey = "Learn-"+key+"-Comment-Like-List";
        return redisTemplate.opsForHash().get(NewKey,field);
    }

    @Override
    public Object getReplyLike(String key, String field) {
        String NewKey = "Learn-"+key+"-Reply-Like-List";
        return redisTemplate.opsForHash().get(NewKey,field);
    }

    @Override
    public void deleteCommentLike(String key, String field) {
        String NewKey = "Learn-"+key+"-Comment-Like-List";
        redisTemplate.opsForHash().delete(NewKey,field);
    }

    @Override
    public void deleteReplyLike(String key, String field) {
        String NewKey = "Learn-"+key+"-Reply-Like-List";
        redisTemplate.opsForHash().delete(NewKey,field);
    }

    private static final String COMMENT_LESSON_KEY = "Comment-Lesson-ID-";
    @Override
    public void cacheCommentLessonId(Integer commentId, Integer lessonId, Integer userId) {
        String key = COMMENT_LESSON_KEY + commentId;
        redisTemplate.opsForValue().set(key, lessonId + "-" + userId);
        redisTemplate.expire(key, 1800, TimeUnit.SECONDS);
    }

    @Override
    public String getCommentLessonIdCache(Integer commentId) {
        String key = COMMENT_LESSON_KEY + commentId;
        Object value = redisTemplate.opsForValue().get(key);
        return Objects.isNull(value) ? "" : (String) value;
    }


    private static final String COMMENT_REPLY_KEY = "Comment-Reply-ID-";
    @Override
    public void cacheCommentReplyId(Integer replyId, Integer commentId, Integer userId) {
        String key = COMMENT_REPLY_KEY + replyId;
        redisTemplate.opsForValue().set(key, commentId + "-" + userId);
        redisTemplate.expire(key, 1800, TimeUnit.SECONDS);
    }

    @Override
    public String getCommentReplyIdCache(Integer replyId) {
        String key = COMMENT_REPLY_KEY + replyId;
        Object value = redisTemplate.opsForValue().get(key);
        return Objects.isNull(value) ? "" : (String) value;
    }
}
