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
}
