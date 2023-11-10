package com.training.learn.entity.respond;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Chapter {
    private int id;
    private String chapter_name;
    private int lesson_id;
}
