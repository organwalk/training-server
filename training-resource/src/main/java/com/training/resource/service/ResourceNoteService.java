package com.training.resource.service;

import com.training.common.entity.DataRespond;
import com.training.common.entity.MsgRespond;
import com.training.resource.entity.request.ResourceNoteReq;

public interface ResourceNoteService {
    // 保存上传笔记
    DataRespond uploadNote(ResourceNoteReq req);
    MsgRespond deleteOneNoteByUser(Integer uid, Integer noteId, String username, String auth);
}
