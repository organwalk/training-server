package com.training.plan.reposoty;

import com.training.plan.entity.table.ChapterTable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonCache {
    void saveChapter(String key, List<ChapterTable> list);

    Object getChapter(String key);

    void deleteChapter(String key);
}
