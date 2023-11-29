package com.push.client;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.concurrent.CompletableFuture;

@HttpExchange("http://localhost:8185/api/learn")
public interface LearnClient {
    @GetExchange("/v1/comment/{comment_id}")
    CompletableFuture<JSONObject> getFatherComment(@PathVariable Integer comment_id);
    @GetExchange("/v1/reply/{reply_id}")
    CompletableFuture<JSONObject> getChildrenComment(@PathVariable Integer reply_id);
}
