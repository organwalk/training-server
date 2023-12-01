package com.training.learn.entity.result;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
public class AllChapter {
    private OverChapter overChapterList;
    private List<ChapterResult> chapterResultList;
}
