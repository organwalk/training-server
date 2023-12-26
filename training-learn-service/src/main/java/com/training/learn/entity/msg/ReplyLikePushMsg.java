package com.training.learn.entity.msg;

import lombok.Data;

import java.util.List;

@Data
public class ReplyLikePushMsg {
    private String msgId;
    private Integer uid;
    private String sourceType = "children_like";
    private String content = "您的回复被点赞";
    private Integer quoteId;
    private List<Integer> receiverIdList;

    public ReplyLikePushMsg(String msgId, Integer uid, Integer quoteId, List<Integer> receiverIdList) {
        this.msgId = msgId;
        this.uid = uid;
        this.quoteId = quoteId;
        this.receiverIdList = receiverIdList;
    }
}
