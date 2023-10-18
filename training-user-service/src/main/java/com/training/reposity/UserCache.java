package com.training.reposity;

import org.springframework.stereotype.Repository;

@Repository
public interface UserCache {
    // 保存用户的access_token
    void saveAccessToken(String username, String accessToken);
}
