package com.training.resource.client;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.HttpExchange;

/**
 * 培训计划服务远程调用
 * by organwalk 2023-10-21
 */
@HttpExchange("http://localhost:8184/api/training")
public interface PlanClient {
    @DeleteExchange("/v4/lesson/test/resource/{resource_lesson_id}")
    JSONObject deleteAllVideoTestByResourceLessonId(@PathVariable Integer resource_lesson_id);
}
