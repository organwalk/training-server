package com.training.learn.service;

import com.training.common.entity.DataRespond;
import com.training.common.entity.MsgRespond;

/**
 * by zhaozhifeng 2023-11-10
 * by organwalk 2023-12-09
 */
public interface ReplyService {
    //回复评论
    DataRespond ReplyComment(String request);

    //回复跟帖
    DataRespond ThreadReply(String request);

    //获取指定评论的回复列表
    DataRespond getReplyList(int comment_id, int user_id, int page_size, int offset);

    //获取指定章节的评论列表
    DataRespond getCommentListByLessonIdAndChapterId(int lesson_id, int chapter_id, int user_id, int page_size, int offset);

    //删除回复
    MsgRespond deleteReply(int id);
    // 获取指定跟帖评论内容
    DataRespond getReplyContent(Integer id);
}
