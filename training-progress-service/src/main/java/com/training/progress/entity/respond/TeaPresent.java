package com.training.progress.entity.respond;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class TeaPresent {
    private double all_total_progress;
    private List<Lesson_Present> lesson_progress;


}
