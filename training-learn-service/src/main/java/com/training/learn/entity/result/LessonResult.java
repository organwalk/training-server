package com.training.learn.entity.result;

import com.training.learn.entity.respond.TeacherInfo;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LessonResult {
    private int id;
    private String lessonName;
    private String lessonDes;
    private String lessonState;
    private double present;
    private int teacher_id;
    private TeacherInfo teacherInfo;
}
