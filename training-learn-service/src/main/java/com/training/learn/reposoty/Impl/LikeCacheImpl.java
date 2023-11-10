package com.training.learn.reposoty.Impl;

import com.training.learn.reposoty.LikeCache;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

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
        return (Integer) redisTemplate.opsForHash().get(NewKey,field);
    }

    @Override
    public Object getReplyLike(String key, String field) {
        String NewKey = "Learn-"+key+"-Reply-Like-List";
        return (Integer) redisTemplate.opsForHash().get(NewKey,field);
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
}
