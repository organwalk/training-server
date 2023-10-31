package com.training.plan.entity.table;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TestTable {
    private int id;
    private String test_title;
    private String test_options_a;
    private String test_options_b;
    private String test_options_c;
    private String test_options_d;
    private String test_options_answer;
    private int test_time;
    private int resource_lesson_id;
    private String test_state;
    private String extra;


}
