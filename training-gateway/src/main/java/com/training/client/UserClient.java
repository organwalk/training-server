package com.training.client;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

/**
 * 获取用户服务内部接口
 * by organwalk 2023-10-19
 */
@HttpExchange("http://localhost:8181/api")
public interface UserClient {
    @GetExchange("/v1/user/auth/{username}")
    JSONObject getUserAuthInfo(@PathVariable String username);
}
