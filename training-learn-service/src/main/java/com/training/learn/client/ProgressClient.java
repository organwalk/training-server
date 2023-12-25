package com.training.learn.client;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.concurrent.CompletableFuture;

/**
 * by zhaozhifeng 2023-11-10
 */
@HttpExchange("http://localhost:8186/api/progress")
public interface ProgressClient {
    @GetExchange("/v1/plan/lesson/persent/{plan_id}/{student_id}/{page_size}/{offset}")
    CompletableFuture<JSONObject> getLessonPersentList(@PathVariable Integer plan_id,
                                                       @PathVariable Integer student_id,
                                                       @PathVariable Integer page_size,
                                                       @PathVariable Integer offset);
    @GetExchange("/v1/lesson/student/list/{lessonId}")
    CompletableFuture<JSONObject> getStudentIdList(@PathVariable Integer lessonId);
}
