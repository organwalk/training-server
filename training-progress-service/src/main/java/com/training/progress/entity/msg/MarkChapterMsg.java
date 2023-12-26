package com.training.progress.entity.msg;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MarkChapterMsg {
    private String msgId;
    private Integer lessonId;
    private Integer chapterId;
    private Integer userId;
    private String nowDateTime;
}
