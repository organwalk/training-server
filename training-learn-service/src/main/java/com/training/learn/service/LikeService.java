package com.training.learn.service;

import com.training.common.entity.MsgRespond;
import com.training.learn.entity.request.LikeReq;

public interface LikeService {
    MsgRespond LikeComment(int user_id,int comment_id,int state);

    MsgRespond LikeReply(int user_id,int reply_id,int state);
}
