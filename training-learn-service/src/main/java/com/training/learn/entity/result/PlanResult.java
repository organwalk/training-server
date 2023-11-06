package com.training.learn.entity.result;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlanResult {
    private int id;
    private String training_title;
    private String training_purpose;
    private String training_start_time;
    private String training_end_time;
    private int dept_id;
    private String training_state;
    private double present;
    private String extra;
}
