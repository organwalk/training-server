package com.training.learn.entity.result;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class TeaQuestionResult {
    private String test_title;
    private String start_datetime;
    private String end_datetime;
    private List<Question> questions;



    @Data
    @AllArgsConstructor
    public static class Question{
        private QuestionDetail question;
        private Map<String,String> options;
        private Integer is_more;

    }

    @Data
    @AllArgsConstructor
    public static class QuestionDetail{
        private Integer id;
        private String content;
        private Integer importance_id;
        private String true_answer;
    }

}
