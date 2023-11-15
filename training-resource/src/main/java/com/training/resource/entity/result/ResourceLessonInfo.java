package com.training.resource.entity.result;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResourceLessonInfo {
    private Integer id;
    private Integer chapterId;
    private String up_datetime;
}
