package com.training.learn.entity.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LikeReq {
    private int user_id;
    private int comment_id;
    private int reply_id;
    @Min(value = 0,message = "lesson_state只能为0或1")
    @Max(value = 1,message = "lesson_state只能为0或1")
    private int state;
    private String create_datetime;
}
