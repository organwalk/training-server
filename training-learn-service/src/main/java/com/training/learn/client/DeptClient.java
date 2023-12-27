package com.training.learn.client;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

/**
 * by zhaozhifeng 2023-11-10
 */
@HttpExchange("http://localhost:8182/api/dept")
public interface DeptClient {
    @GetExchange("/v1/department/info/{dept_id}")
    JSONObject getDeptInfo(@PathVariable int dept_id);

    @GetExchange("/v1/department/{uid}")
    JSONObject getDeptIdByUserId(@PathVariable int uid);
}
