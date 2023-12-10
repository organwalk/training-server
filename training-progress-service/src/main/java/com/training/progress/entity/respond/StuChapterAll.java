package com.training.progress.entity.respond;

import com.training.progress.entity.result.Chapter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
public class StuChapterAll {
    private int lesson_id;
    private int student_id;
    private int overChapter_id;
    private Chapter chapter;
    private String completionDate;

}
