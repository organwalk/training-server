package com.training.learn.entity.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor(onConstructor_={@JsonIgnore})
public class AnswerRequest {

    private List<Answer> answers;

    @Data
    @AllArgsConstructor
    public static class Answer{
        private Integer q_id;
        private String answer;
    }
}
