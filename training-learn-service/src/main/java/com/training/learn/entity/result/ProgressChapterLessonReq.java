package com.training.learn.entity.result;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProgressChapterLessonReq {
    @Digits(message = "lesson_id必须为数字",integer = Integer.MAX_VALUE,fraction = 0)
    private int lesson_id;
    @Digits(message = "student_id必须为数字",integer = Integer.MAX_VALUE,fraction = 0)
    private int student_id;
    @Digits(message = "over_chapter_id必须为数字",integer = Integer.MAX_VALUE,fraction = 0)
    private int over_chapter_id;
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "章节完成时间格式必须为yyyy-mm-dd")
    private String completion_date;
}
