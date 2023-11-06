package com.training.learn.client;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange("http://localhost:8184/api/training")
public interface PlanClient {

    @GetExchange("/v1/plan/info/{plan_id}")
    JSONObject getPlanInfoById(@PathVariable int plan_id);

}
