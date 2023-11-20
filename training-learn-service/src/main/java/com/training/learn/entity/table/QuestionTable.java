package com.training.learn.entity.table;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class QuestionTable {
    private Integer id;
    private String question_content;
    private int importance_id;
    private String true_answer;
    private int test_id;
}
