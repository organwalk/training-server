package com.training.resource.service;

import com.training.common.entity.DataRespond;
import com.training.common.entity.MsgRespond;
import com.training.resource.entity.request.TagReq;

/**
 * 与分类标签有关的业务接口
 * by organwalk 2023-10-21
 */
public interface TagService {

    // 创建资源分类标签
    MsgRespond createTag(TagReq req);

    // 编辑资源分类标签
    MsgRespond updateTag(Integer tag_id, String tagName);

    // 删除指定资源分类标签
    MsgRespond deleteTagByTagId(Integer tagId);

    // 查看指定部门下的资源分类标签列表
    DataRespond getTagListByDeptId(Integer deptId);

}
