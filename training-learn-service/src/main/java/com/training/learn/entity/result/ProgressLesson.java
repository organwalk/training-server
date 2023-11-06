package com.training.learn.entity.result;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@TableName("t_progress_lesson")
public class ProgressLesson {
    private int id;
    private int lesson_id;
    private int student_id;
    private int over_chapter_sum;
    private int lesson_chapter_sum;
}
