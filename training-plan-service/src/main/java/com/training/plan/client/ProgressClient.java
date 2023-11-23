package com.training.plan.client;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange("http://localhost:8186/api/progress")
public interface ProgressClient {
    @PostExchange("/v2/lesson/student/{lesson_id}/{student_id}/{over_chapter_sum}/{lesson_chapter_sum}")
    JSONObject insertProgress(@PathVariable Integer lesson_id,@PathVariable Integer student_id,@PathVariable Integer over_chapter_sum,@PathVariable Integer lesson_chapter_sum);

    @PostExchange("/v2/plan/lesson/teacher/{plan_id}/{teacher_id}/{lesson_id}")
    JSONObject insertInProPlan(@PathVariable Integer plan_id,@PathVariable Integer teacher_id,@PathVariable Integer lesson_id);

    @GetExchange("/v1/plan/lesson/{plan_id}/{page_size}/{offset}}")
    JSONObject getLessonIdByPlanId(@PathVariable Integer plan_id,
                                   @PathVariable Integer page_size,
                                   @PathVariable Integer offset);
}
