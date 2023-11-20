package com.training.learn.entity.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TestReq {
    @NotBlank(message = "test_title不能为空")
    @Size(min=1, max=15, message="试题标题长度必须在1到15个字符之间")
    private String test_title;

    @Min(value=1, message = "课程ID必须大于0")
    private int lesson_id;

    @Min(value=1, message = "教师ID必须大于0")
    private int teacher_id;

    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}", message = "试题开始时间格式必须为yyyy-MM-dd hh:mm:ss")
    private String start_datetime;

    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}", message = "试题结束时间格式必须为yyyy-MM-dd hh:mm:ss")
    private String end_datetime;


    private String create_datetime;

    @Min(value =0, message = " isRelease只能为0或1")
    @Max(value =1, message = " isRelease只能为0或1")
    private Integer isRelease;
}
