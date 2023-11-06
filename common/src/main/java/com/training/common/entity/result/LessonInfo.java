package com.training.common.entity.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LessonInfo {
    private Integer id;
    private String lessonName;
    private String lessonDes;
    private Integer teacher_id;
}
