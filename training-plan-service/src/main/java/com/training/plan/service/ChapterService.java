package com.training.plan.service;

import com.training.common.entity.DataRespond;
import com.training.common.entity.MsgRespond;


/**
 * by zhaozhifeng 2023-10-31
 */
public interface ChapterService {
    MsgRespond insertChapter(String name,int lesson_id);

    DataRespond getChapterLessonId(int lessonId);

    MsgRespond updateChapterName(String chapter_name,int id);

    MsgRespond deleteChapterById(int id);

    DataRespond getChapterDetail(Integer chapterId);
}
