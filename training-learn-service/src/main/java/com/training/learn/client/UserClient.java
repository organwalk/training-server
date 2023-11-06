package com.training.learn.client;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.training.common.entity.req.UserInfoListReq;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
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
    //获取用户信息列表
    @GetExchange("/v1/info/list")
    JSONArray getUserInfoByUidList(@RequestBody UserInfoListReq req);
}
