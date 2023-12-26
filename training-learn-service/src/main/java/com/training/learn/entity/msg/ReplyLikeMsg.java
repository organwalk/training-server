package com.training.learn.entity.msg;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReplyLikeMsg {
    private String msgId;
    private Integer userId;
    private Integer replyId;
    private Integer commentId;
    private Integer replyUser;
    private Integer state;
    private String nowDateTime;
}
