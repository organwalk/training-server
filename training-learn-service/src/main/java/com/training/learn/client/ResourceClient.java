package com.training.learn.client;

import com.alibaba.fastjson.JSONObject;
import com.training.learn.entity.respond.ResourceNoteReq;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

/**
 * by zhaozhifeng 2023-11-10
 */
@HttpExchange("http://localhost:8183/api/resource")
public interface ResourceClient {

    @GetMapping("/v1/detail/note/{note_id}")
    JSONObject getNoteDetail(@PathVariable Integer note_id);

    @DeleteExchange("/v1/file/note/{user_id}/{note_id}")
    JSONObject deleteNoteResource(@PathVariable int user_id,@PathVariable int note_id);
}
