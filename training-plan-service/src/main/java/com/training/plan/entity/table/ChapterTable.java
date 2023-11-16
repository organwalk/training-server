package com.training.plan.entity.table;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@TableName("t_lesson_chapter")
public class ChapterTable {
    private int id;
    private String chapterName;
    private int lesson_id;
}
