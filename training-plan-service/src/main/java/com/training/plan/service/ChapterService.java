package com.training.plan.service;

import com.training.common.entity.DataRespond;
import com.training.common.entity.MsgRespond;



public interface ChapterService {
    MsgRespond insertChapter(String name,int lesson_id);

    DataRespond getChapterLessonId(int lessonId);

    MsgRespond updateChapterName(String chapter_name,int id);

    MsgRespond deleteChapterById(int id);

    MsgRespond deleteAllChapterByLessonId(int lesson_id);
}
