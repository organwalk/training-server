package com.training.plan.entity.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LessonReq {
    @NotBlank(message = "lesson_name不能为空")
    @Size(max = 15,message = "lesson_name不能超过20字")
    private String lesson_name;
    @NotBlank(message = "lesson_des不能为空")
    @Size(max = 150,message = "lesson_des不能超过150字")
    private String lesson_des;
    @Digits(message = "teacher_id必须为数字",integer = Integer.MAX_VALUE,fraction = 0)
    private int teacher_id;
    @Min(value = 0,message = "lesson_state只能为0或1")
    @Max(value = 1,message = "lesson_state只能为0或1")
    private int lesson_state;
}
