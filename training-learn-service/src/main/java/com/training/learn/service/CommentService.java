package com.training.learn.service;

import com.training.common.entity.MsgRespond;

public interface CommentService {
    MsgRespond insertCommentOne(String request);

    MsgRespond insertCommentTwo(String request);
}
