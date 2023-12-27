package com.training.resource.exceptions;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.training.common.entity.DataFailRespond;
import com.training.common.entity.DataRespond;
import com.training.common.entity.MsgRespond;

public class GlobeBlockException {
    public static DataRespond blockedGetNoteDetail(Integer note_id, BlockException e){
        return new DataFailRespond("资源服务异常，无法正常获取笔记详情");
    }
    public static MsgRespond blockedDeleteOneNote(Integer user_id, Integer note_id, String username, String auth, BlockException e){
        return MsgRespond.fail("资源服务异常，无法正常删除学习笔记");
    }
    public static MsgRespond blockedDeleteOneLessonResource(Integer chapter_id, BlockException e){
        return MsgRespond.fail("资源服务异常，无法正常删除课程教材");
    }
}
