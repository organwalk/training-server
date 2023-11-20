package com.training.learn.entity.result;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class StuQuestionResult {
    private String test_title;
    private String start_datetime;
    private String end_datetime;
    private List<Question> questions;


    @Data
    @AllArgsConstructor
    public static class Question{
        private Integer id;
        private String content;
        private Map<String,String> options;
        private Integer is_more;
    }
}
