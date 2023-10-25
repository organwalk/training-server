package com.training.plan.entity.table;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.training.plan.entity.result.DeptInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
/**
 * 定义t_training_plan表映射实体
 */
@Data
@AllArgsConstructor
@TableName("t_training_plan")
public class TrainingPlanTable {
    private int id;
    private String training_title;
    private String training_purpose;
    private String training_start_time;
    private String training_end_time;
    private int dept_id;
    private String training_state;
    private String extra;
}
