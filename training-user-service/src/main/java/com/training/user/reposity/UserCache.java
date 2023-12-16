package com.training.user.reposity;

import org.springframework.stereotype.Repository;
/**
 * 计划服务的缓存接口实现
 * by organwalk 2023-10-18
 */
@Repository
public interface UserCache {

    // 保存用户的access_token
    void saveAccessToken(String username, String accessToken);

    // 获取用户的access_token
    String getAccessToken(String username);

    // 销毁用户的access_token
    void deleteAccessToken(String username);
}
