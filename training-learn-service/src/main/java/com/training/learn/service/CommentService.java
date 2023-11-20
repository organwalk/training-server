package com.training.learn.service;

import com.training.common.entity.MsgRespond;
import com.training.learn.entity.request.NoteReq;

public interface CommentService {
    //在指定课程下发布评论
    MsgRespond insertCommentOne(String request);
    //在指定课程章节下发布评论
    MsgRespond insertCommentTwo(String request);
    //删除评论
    MsgRespond DeleteComment(int comment_id);
    //添加笔记
    MsgRespond insertNote(Integer user_id,Integer lesson_id,Integer chapter_id,NoteReq req);
}
