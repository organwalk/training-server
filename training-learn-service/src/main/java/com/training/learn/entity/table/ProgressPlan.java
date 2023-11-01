package com.training.learn.entity.table;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@TableName("t_progress_plan")
public class ProgressPlan {
    private int id;
    private int plan_id;
    private int teacher_id;
    private int lesson_id;
}
