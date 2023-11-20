package com.training.learn.entity.request;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor(onConstructor_={@JsonIgnore})
public class QuestionReq {

    private List<Question> questions;


    @Data
    @AllArgsConstructor
    public static class Question {

        private QuestionDetails question;

        private Map<String, String> options;
        @Min(0)
        @Max(1)
        private int is_more;
        public Map<String,String> getOptions() {
            if (options == null || !options.containsKey("A") || !options.containsKey("B")) {
                throw new IllegalArgumentException("选项必须为两个或以上!");
            }
            return options;
        }
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
