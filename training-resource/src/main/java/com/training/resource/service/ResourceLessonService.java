package com.training.resource.service;

import com.training.common.entity.MsgRespond;
import com.training.resource.entity.request.ResourceLessonReq;

/**
 * 定义课程教材相关业务
 * by organwalk 2023-11-03
 */
public interface ResourceLessonService {
    // 上传教材资源文件
    MsgRespond uploadResourceLesson(ResourceLessonReq req);
    // 重传课程教材
    MsgRespond reUploadResourceLesson(ResourceLessonReq req);
    // 删除指定课程章节教材文件
    MsgRespond deleteOneLessonResource(Integer teacherId, Integer lessonId, Integer chapterId);
    // 删除指定课程下所有教材文件
    MsgRespond deleteAllLessonResource(Integer teacherId, Integer lessonId);
}
