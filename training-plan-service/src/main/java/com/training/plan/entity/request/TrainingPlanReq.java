package com.training.plan.entity.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 定义创建计划请求的映射实体

 */
@Data
@AllArgsConstructor
public class TrainingPlanReq {
    @NotBlank(message = "trainingTitle不能为空")
    private String training_title;
    @NotBlank(message = "trainingPurpose不能为空")
    private String training_purpose;
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "培训计划结束时间格式必须为yyyy-mm-dd")
    private String training_start_time;
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "培训计划结束时间格式必须为yyyy-mm-dd")
    private String training_end_time;
    @Min(value = 0,message = "deptId必须大于等于0")
    private int dept_id;

    private String training_state;

}
