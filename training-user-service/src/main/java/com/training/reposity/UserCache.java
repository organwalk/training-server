package com.training.reposity;

import org.springframework.stereotype.Repository;

@Repository
public interface UserCache {

    // 保存用户的access_token
    void saveAccessToken(String username, String accessToken);

    // 获取用户的access_token
    String getAccessToken(String username);

    // 销毁用户的access_token
    void deleteAccessToken(String username);
}
