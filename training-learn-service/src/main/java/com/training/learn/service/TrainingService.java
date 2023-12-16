package com.training.learn.service;

import com.training.common.entity.DataRespond;

/**
 * by linguowei 2023-11-06
 */
public interface TrainingService {
    //获取指定学生的计划列表
    DataRespond getPlanListByStuId(int student_id,int page_size,int offset);
    //获取指定培训计划下的课程
    DataRespond getLessonByPIdAndStuId(int plan_id,int student_id,int page_size,int offset);
    //获取指定课程下的章节
    DataRespond getChapterByStuIdAndLessId(int student_id,int lesson_id);

}
