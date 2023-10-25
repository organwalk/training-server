package com.training.plan.entity.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 定义视频创建视频中测试题请求实体
 */
@Data
public class TestReq {
    @NotBlank(message = "标题不能为空")
    private String test_title;
    @NotBlank(message = "选项A不能为空")
    private String test_options_a;
    @NotBlank(message = "选项B不能为空")
    private String test_options_b;
    @NotBlank(message = "选项C不能为空")
    private String test_options_c;
    @NotBlank(message = "选项D不能为空")
    private String test_options_d;
    @Pattern(regexp = "^(a|b|c|d)(,(a|b|c|d))*$", message = "答案格式不正确,只允许\"a\"或\"a,b,c,d\"两种格式的数据")
    private String test_options_answer;
    @Pattern(regexp = "^[0-9]+$", message = "题目出现的实践只能为数字，单位为秒")
    private String test_time;

}
