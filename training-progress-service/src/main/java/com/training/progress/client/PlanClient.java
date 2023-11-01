package com.training.progress.client;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange("http://localhost:8184/api/training")
public interface PlanClient {
    //获取指定课程
    @GetExchange("/v1/lesson/info/{lesson_id}")
    JSONObject getLessonInfo(int lesson_id);
    //获取指定章节

    @GetExchange("/v1/lesson/chapter/{lesson_id}")
    JSONArray getAllChapterByLessonId(int lesson_id);
}
