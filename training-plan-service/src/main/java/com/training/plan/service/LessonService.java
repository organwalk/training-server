package com.training.plan.service;

import com.training.common.entity.DataRespond;
import com.training.common.entity.MsgRespond;
import com.training.plan.entity.request.LessonReq;
import com.training.plan.entity.request.LessonUpdate;

public interface LessonService {
    //加入课程
    MsgRespond insertLesson(LessonReq req);
    //获取指定教师的所有课程
    DataRespond getTeaAllLess(int teacher_id,int page_size,int offset);
    //获取指定课程信息
    DataRespond getLessInfoById(int id);
    //编辑指定课程
    MsgRespond updateLesson(LessonUpdate req, int id, int teacher_id);
    //删除指定课程
    MsgRespond DeleteLessonById(int id);
    //删除指定教师的所有课程
    MsgRespond DeleteLessonByTId(int teacher_id);
    //设定课程状态为发布
    MsgRespond updateState(int id);
}
