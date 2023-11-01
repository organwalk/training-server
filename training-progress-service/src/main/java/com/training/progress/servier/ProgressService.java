package com.training.progress.servier;

import com.training.common.entity.DataRespond;
import com.training.common.entity.MsgRespond;

public interface ProgressService {
    MsgRespond MarkChapterComplete(int lesson_id, int chapter_id, int student_id);

    MsgRespond insertStuLessonProgress(int lesson_id, int student_id, int over_chapter_sum, int lesson_chapter_sum);

    DataRespond getStuAllByLessonId(int lesson_id, int page_size, int offset);

    DataRespond getAllPlanProgressList(int page_size, int offset);
}
