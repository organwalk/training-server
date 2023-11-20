package com.training.learn.entity.result;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ScoreResult {

    private student_info student_info;
    private score score;

    @Data
    @AllArgsConstructor
    public static class student_info{
        private int id;
        private String real_name;
    }


    @Data
    @AllArgsConstructor
    public static class score{
        private double composite_score;
        private double must_type_composite_score;
        private double important_type_composite_score;
        private double normal_type_composite_score;
        private String level;
    }
}
