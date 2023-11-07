package com.training.resource.client;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

/**
 * 获取培训管理服务内部接口
 * by organwalk 2023-11-03
 */
@HttpExchange("http://localhost:8184/api/training")
public interface TrainingClient {
    @GetExchange("/v1/lesson/{teacher_id}/{page_size}/{offset}")
    JSONObject getLessonListByTeacher(@PathVariable Integer teacher_id, @PathVariable Integer page_size, @PathVariable Integer offset);
    @GetExchange("/v1/lesson/chapter/{lesson_id}")
    JSONObject getChapterListByLesson(@PathVariable Integer lesson_id);
}
