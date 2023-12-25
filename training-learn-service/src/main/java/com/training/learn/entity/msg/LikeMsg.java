package com.training.learn.entity.msg;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LikeMsg {
    private String msgId;
    private Integer userId;
    private Integer commentId;
    private Integer lessonId;
    private Integer commentUser;
    private Integer state;
    private String nowDateTime;
}
