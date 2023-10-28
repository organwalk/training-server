package com.training.plan.entity.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlanUpdateReq {
    @NotBlank(message = "trainingTitle不能为空")
    private String training_title;
    @NotBlank(message = "trainingPurpose不能为空")
    private String training_purpose;
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "培训计划结束时间格式必须为yyyy-mm-dd")
    private String training_end_time;
}
