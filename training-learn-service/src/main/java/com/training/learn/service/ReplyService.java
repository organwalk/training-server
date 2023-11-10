package com.training.learn.service;

import com.training.common.entity.DataRespond;
import com.training.common.entity.MsgRespond;

public interface ReplyService {
    MsgRespond ReplyComment(String request);

    MsgRespond ThreadReply(String request);

    DataRespond getCommentList(int lesson_id,int user_id,int page_size,int offset);

    DataRespond getReplyList(int comment_id,int user_id,int page_size,int offset);

    DataRespond getCommentListByLessonIdAndChapterId(int lesson_id,int chapter_id,int user_id,int page_size,int offset);
}
