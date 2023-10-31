package com.training.plan.entity.table;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChapterTable {
    private int id;
    private String chapter_name;
    private int lesson_id;
}
