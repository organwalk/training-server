package com.training.progress.entity.respond;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StuPresent {
    private int lesson_id;
    private int student_id;
    private double present;
}
