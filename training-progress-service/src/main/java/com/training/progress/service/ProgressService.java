package com.training.progress.service;

import com.training.common.entity.DataRespond;
import com.training.common.entity.MsgRespond;

/**
 * by zhaozhifeng 2023-11-06
 */
public interface ProgressService {
    MsgRespond MarkChapterComplete(int lesson_id, int chapter_id, int student_id);

    MsgRespond insertStuLessonProgress(int lesson_id, int student_id, int over_chapter_sum, int lesson_chapter_sum);

    DataRespond getStuAllByLessonId(int lesson_id, int page_size, int offset);

    DataRespond getAllPlanProgressList(int page_size, int offset);

    DataRespond getAllStuLessonPresent(int lesson_id,int page_size,int offset);

    MsgRespond insertProgressPlan(int plan_id,Integer lesson_id,int teacher_id);

    DataRespond getTeaAllPresent(int planId, int teacher_id,String auth,String username);

    DataRespond getAllPlanPresent(int page_size,int offset);

    DataRespond gerLessonIdListByPlanId(int plan_id,int page_size,int offset);

    DataRespond getAllLessonPresentByStuId(int student_id,int plan_id,int page_size,int offset);

    DataRespond getChapterListByStuIdAndLessonId(int student_id,int lesson_id);

    MsgRespond updateChapterSum(Integer sum,Integer lesson_id);

    DataRespond getStudentIdList(Integer lessonId);

}
