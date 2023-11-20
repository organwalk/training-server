package com.training.learn.entity.result;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RankResult {
    private double composite_score;
    private String level;
    private Integer rank;
}
