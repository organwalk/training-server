package com.training.plan.entity.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LessonUpdate {
    @NotBlank(message = "lesson_name不能为空")
    @Pattern(regexp = "^.{0,15}$", message = "lesson_name长度不能超过15个字符")
    private String lesson_name;
    @NotBlank(message = "lesson_des不能为空")
    @Size(max = 150,message = "lesson_des不能超过150字")
    private String lesson_des;
}
