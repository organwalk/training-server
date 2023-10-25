package com.training.plan.entity.table;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
/**
 * 定义t_training_plan_student表映射实体
 */
@Data
@AllArgsConstructor
@TableName("t_training_plan_student")
public class TrainingPlanStudentTable {
    private int id;
    private int training_student_id;
    private int training_plan_id;
}
