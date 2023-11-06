package com.training.progress.entity.respond;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Lesson_Present {
    private int id;
    private String lesson_name;
    private double total_progress;
}
