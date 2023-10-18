package com.training.reposity.Impl;

import com.training.reposity.UserCache;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 用户服务的缓存接口实现
 * by organwalk 2023-10-18
 */
@Component
@AllArgsConstructor
public class UserCacheImpl implements UserCache {
    private final static String ACCESS_TOKEN_KEY = "Training-User-Service-Token";
    private final RedisTemplate redisTemplate;

    /**
     * 将access_token保存于缓存中
     *
     * @param username    用户名
     * @param accessToken 通行令牌
     *                    <p>
     *                    by organwalk 2023-10-18
     */
    @Override
    public void saveAccessToken(String username, String accessToken) {
        redisTemplate.opsForHash().put(ACCESS_TOKEN_KEY, username, accessToken);
    }
}
