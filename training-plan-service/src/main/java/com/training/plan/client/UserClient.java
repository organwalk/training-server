package com.training.plan.client;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
/**
 * 获取用户服务内部接口
 */
@HttpExchange("http://localhost:8181/api/user")
public interface UserClient {
    // 获取指定用户账号信息
    @GetExchange("/v1/account/{uid}")
    JSONObject getUserAccountByUid(@PathVariable Integer uid);
}
