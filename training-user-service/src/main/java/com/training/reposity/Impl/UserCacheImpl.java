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
    private final RedisTemplate<String, Object> redisTemplate;
    private final static String ACCESS_TOKEN_KEY = "Training-User-Service-Token";


    /**
     * 将access_token保存于缓存中
     * @param username    用户名
     * @param accessToken 通行令牌
     * by organwalk 2023-10-18
     */
    @Override
    public void saveAccessToken(String username, String accessToken) {
        redisTemplate.opsForHash().put(ACCESS_TOKEN_KEY, username, accessToken);
    }

    /**
     * 根据username获取通行令牌
     * @param username 用户名
     * @return 字符串形式的通行令牌
     * by organwalk 2023-10-19
     */
    @Override
    public String getAccessToken(String username) {
        String accessToken = (String) redisTemplate.opsForHash().get(ACCESS_TOKEN_KEY, username);
        return accessToken != null ? accessToken : "";
    }
}
