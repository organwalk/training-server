package com.training.learn.entity.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class CacheReq {
    @Size(min=1, max=15, message="试题标题长度必须在1到15个字符之间")
    private String test_title;

    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}", message = "试题开始时间格式必须为yyyy-MM-dd hh:mm:ss")
    private String start_datetime;

    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}", message = "试题结束时间格式必须为yyyy-MM-dd hh:mm:ss")
    private String end_datetime;

    private List<Question> questions;


    @Data
    @AllArgsConstructor
    public static class Question {

        private QuestionDetails question;

        private Map<String, String> options;

        @Min(0)
        @Max(1)
        private int is_more;

    }

    @Data
    @AllArgsConstructor
    public static class QuestionDetails {

        @Size(max = 200)
        private String content;

        @Min(1)
        @Max(3)
        private int importance_id;


        private String true_answer;
    }

}
