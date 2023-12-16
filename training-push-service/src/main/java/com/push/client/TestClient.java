package com.push.client;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.concurrent.CompletableFuture;

/**
 * 远程调用考核评估服务
 * by organwalk 2023-11-29
 */
@HttpExchange("http://localhost:8185/api/learn")
public interface TestClient {
    @GetExchange("/v1/test/info/{test_id}")
    CompletableFuture<JSONObject> getTestInfo(@PathVariable Integer test_id);
}
