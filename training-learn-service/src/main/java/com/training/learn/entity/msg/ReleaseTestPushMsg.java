package com.training.learn.entity.msg;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
public class ReleaseTestPushMsg {
    private String msgId;
    private Integer uid;
    private String sourceType = "test";
    private Content content;
    private Integer quoteId;
    private List<Integer> receiverIdList;

    @Data
    @AllArgsConstructor
    public static class Content{
        private String lessonName;
    }

    public ReleaseTestPushMsg(String msgId, Integer uid, Content content, Integer quoteId, List<Integer> receiverIdList) {
        this.msgId = msgId;
        this.uid = uid;
        this.content = content;
        this.quoteId = quoteId;
        this.receiverIdList = receiverIdList;
    }
}
