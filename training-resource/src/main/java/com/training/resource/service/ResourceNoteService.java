package com.training.resource.service;

import com.training.common.entity.DataRespond;
import com.training.common.entity.MsgRespond;
import com.training.resource.entity.request.ResourceNoteReq;
import org.springframework.http.ResponseEntity;

/**
 * 与学习笔记有关的业务
 * by organwalk 2023-10-21
 */
public interface ResourceNoteService {
    // 保存上传笔记
    DataRespond uploadNote(ResourceNoteReq req);
    // 删除学习笔记
    MsgRespond deleteOneNoteByUser(Integer uid, Integer noteId, String username, String auth);
    // 删除指定章节的学习笔记
    MsgRespond deleteNoteByChapter(Integer lessonId, Integer chapterId);
    // 删除指定课程的学习笔记
    MsgRespond deleteNoteByLesson(Integer lessonId);
    // 获取指定笔记的内容
    ResponseEntity<?> getNoteById(Integer noteId);
}
