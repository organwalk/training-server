package com.training.learn.entity.result;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AllChapter {
    private List<OverChapter> overChapterList;
    private List<ChapterResult> chapterResultList;
}
