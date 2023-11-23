package com.training.resource.client;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange("http://localhost:8184/api/training")
public interface PlanClient {
    @DeleteExchange("/v4/lesson/test/resource/{resource_lesson_id}")
    void deleteAllVideoTestByResourceLessonId(@PathVariable Integer resource_lesson_id);
}
