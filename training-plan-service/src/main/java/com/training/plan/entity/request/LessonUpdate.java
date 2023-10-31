package com.training.plan.entity.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LessonUpdate {
    @NotBlank(message = "lesson_name不能为空")
    @Size(max = 15,message = "lesson_name不能超过15字")
    private String lesson_name;
    @NotBlank(message = "lesson_name不能为空")
    @Size(max = 15,message = "lesson_name不能超过15字")
    private String lesson_des;
}
