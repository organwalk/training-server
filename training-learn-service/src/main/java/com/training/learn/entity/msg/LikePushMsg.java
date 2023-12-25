package com.training.learn.entity.msg;

import lombok.Data;

import java.util.List;

@Data
public class LikePushMsg {
    private String msgId;
    private Integer uid;
    private String sourceType = "father_like";
    private String content = "您的评论被点赞";
    private Integer quoteId;
    private List<Integer> receiverIdList;

    public LikePushMsg(String msgId, Integer uid, Integer quoteId, List<Integer> receiverIdList) {
        this.msgId = msgId;
        this.uid = uid;
        this.quoteId = quoteId;
        this.receiverIdList = receiverIdList;
    }
}
