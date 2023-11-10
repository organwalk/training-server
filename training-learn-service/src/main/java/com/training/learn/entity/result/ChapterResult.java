package com.training.learn.entity.result;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChapterResult {
    private int id;
    private String chapterName;
    private boolean over;
}
