package com.training.learn.exceptions;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.training.common.entity.DataFailRespond;
import com.training.common.entity.DataRespond;

public class GlobeBlockException {
    public static DataRespond blockedGetComment(Integer comment_id, BlockException e){
        return new DataFailRespond("学习相关服务异常，无法正常获取主评论内容");
    }
    public static DataRespond blockedGetReply(Integer reply_id, BlockException e){
        return new DataFailRespond("学习相关服务异常，无法正常获取回复内容");
    }
    public static DataRespond blockedGetTestInfo(Integer testId, BlockException e){
        return new DataFailRespond("学习相关服务异常，无法正常获取试卷信息");
    }
}
