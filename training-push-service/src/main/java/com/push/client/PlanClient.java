package com.push.client;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.concurrent.CompletableFuture;

@HttpExchange("http://localhost:8184/api/training")
public interface PlanClient {

    @GetExchange("/v1/plan/info/{plan_id}")
    CompletableFuture<JSONObject> getPlanInfoById(@PathVariable int plan_id);

    @GetExchange("/v1/lesson/info/{lesson_id}")
    JSONObject getLessonInfo(@PathVariable int lesson_id);

    @GetExchange("/v1/lesson/chapter/{lesson_id}")
    JSONObject getChapterByLId(@PathVariable int lesson_id);
}
