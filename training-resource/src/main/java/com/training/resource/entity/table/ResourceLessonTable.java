package com.training.resource.entity.table;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@TableName("t_resource_lesson")
@AllArgsConstructor
public class ResourceLessonTable {
    private Integer id;
    private Integer lessonId;
    private Integer teacherId;
    private Integer chapterId;
    private String resourcePath;
    private String upDatetime;
}
