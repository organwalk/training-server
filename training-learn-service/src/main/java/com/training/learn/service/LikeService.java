package com.training.learn.service;

import com.training.common.entity.MsgRespond;

/**
 * by zhaozhifeng 2023-11-10
 */
public interface LikeService {
    //点赞或取消点赞评论
    MsgRespond LikeComment(int user_id,int comment_id,int state);
    //点赞或取消点赞回复
    MsgRespond LikeReply(int user_id,int reply_id,int state);
}
