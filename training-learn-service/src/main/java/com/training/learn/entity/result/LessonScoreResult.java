package com.training.learn.entity.result;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LessonScoreResult {
    private int test_id;
    private double avg_composite_score;
    private double avg_must_type_composite_score;
    private double avg_important_type_composite_score;
    private double avg_normal_type_composite_score;
}
