package com.training.plan.client;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange("http://localhost:8183/api/resource")
public interface ResourceClient {
    @DeleteExchange("/v2/lesson/chapter/{chapter_id}")
    JSONObject deleteOneLessonResource(@PathVariable Integer chapter_id);
    @DeleteExchange("/v2/resource/lesson/{lesson_id}")
    void deleteAllLessonResource(@PathVariable Integer lesson_id);
}
