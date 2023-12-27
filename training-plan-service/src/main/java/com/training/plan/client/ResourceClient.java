package com.training.plan.client;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.HttpExchange;

/**
 * 进度服务接口远程调用
 * by organwalk 2023-11-23
 */
@HttpExchange("http://localhost:8183/api/resource")
public interface ResourceClient {
    @DeleteExchange("/v2/lesson/chapter/{chapter_id}")
    JSONObject deleteOneLessonResource(@PathVariable Integer chapter_id);
}
