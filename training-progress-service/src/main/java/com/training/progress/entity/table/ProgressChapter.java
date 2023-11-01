package com.training.progress.entity.table;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@TableName("t_progress_chapter")
public class ProgressChapter {
    private int id;
    private int lesson_id;
    private int student_id;
    private int over_chapter_id;
    private String completion_date;
}
