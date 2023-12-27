package com.training.resource.service;

import com.training.common.entity.DataRespond;
import com.training.common.entity.MsgRespond;
import com.training.resource.entity.request.ResourceLessonReq;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

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
    MsgRespond deleteOneLessonResource(Integer chapterId);
    // 根据课程教材ID获取教材
    ResponseEntity<?> getResourceLessonById(String rangeString, Integer rlId, String random_str);

    // 获取指定教师的教材列表
    DataRespond getResourceLessonByLessonId(Integer lessonId);

    // 获取指定教材资源ID
    DataRespond getResourceLessonId(Integer lessonId, Integer chapterId);

    // 获取指定教材资源类型
    DataRespond getResourceLessonType(Integer resourceId);
}
