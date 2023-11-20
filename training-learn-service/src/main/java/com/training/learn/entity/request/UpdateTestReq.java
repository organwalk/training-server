package com.training.learn.entity.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateTestReq {
    @Size(min=1, max=15, message="试题标题长度必须在1到15个字符之间")
    private String test_title;

    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}", message = "试题开始时间格式必须为yyyy-MM-dd hh:mm:ss")
    private String start_datetime;

    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}", message = "试题结束时间格式必须为yyyy-MM-dd hh:mm:ss")
    private String end_datetime;
}
