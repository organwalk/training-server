package com.training.learn.entity.msg;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommentMsg {
    private String msgId;
    private Integer userId;
    private Integer lessonId;
    private Integer chapterId;
}
