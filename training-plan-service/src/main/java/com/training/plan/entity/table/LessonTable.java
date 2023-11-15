package com.training.plan.entity.table;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@TableName("t_lesson")
public class LessonTable {
    private Integer id;
    private String lesson_name;
    private String lesson_des;
    private int teacher_id;
    private int lesson_state;//0：未发布 1：发布
    private String extra;
}
