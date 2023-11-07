package com.training.learn.entity.respond;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LessonProgress {
    private int id;
    private int lesson_id;
    private int student_id;
    private int over_chapter_sum;
    private int lesson_chapter_sum;
}
