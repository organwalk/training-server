package com.training.resource.service.Impl;

import com.alibaba.fastjson.JSONObject;
import com.training.common.entity.DataFailRespond;
import com.training.common.entity.DataRespond;
import com.training.common.entity.DataSuccessRespond;
import com.training.common.entity.MsgRespond;
import com.training.resource.client.DeptClient;
import com.training.resource.entity.request.TagReq;
import com.training.resource.entity.respond.TagRespond;
import com.training.resource.mapper.ResourceNormalMapper;
import com.training.resource.mapper.TagMapper;
import com.training.resource.service.TagService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * 定义Tag相关业务接口的具体实现
 * by organwalk 2023-10-21
 */
@Service
@AllArgsConstructor
@Transactional
public class TagServiceImpl implements TagService {
    private final TagMapper tagMapper;
    private final DeptClient deptClient;
    private final ResourceNormalMapper resourceNormalMapper;
    /**
     * 创建资源分类标签
     * @param req 请求实体，包含部门ID和分类标签名
     * @return 根据处理结果返回提示消息
     * by organwalk 2023-10-21
     */
    @Override
    public MsgRespond createTag(TagReq req) {
        Integer deptId = req.getDept_id();
        String tagName = req.getTag_name();
        // 检查部门是否存在
        JSONObject deptInfo = deptClient.getDeptExistStatus(deptId);
        if (Objects.equals(deptInfo.getInteger("code"), 5005)){
            return MsgRespond.fail(deptInfo.getString("msg"));
        }
        // 检查指定部门下标签名是否已经存在
        String tagMark = tagMapper.selectTagExistByDeptIdAndTagName(deptId, tagName);
        if (Objects.equals(tagMark, tagName)){
            return MsgRespond.fail("当前指定的分类标签已存在，请修改后重试");
        }
        // 创建标签
        tagMapper.insertTag(req);
        return MsgRespond.success("已成功在此部门下创建分类标签");
    }

    /**
     * 编辑资源分类标签
     * @param tag_id 标签ID
     * @param tagName 分类标签名
     * @return 根据处理结果返回消息提示
     * by organwalk 2023-10-21
     */
    @Override
    public MsgRespond updateTag(Integer tag_id, String tagName) {
        // 检查指定标签是否存在
        Integer tagMark = tagMapper.selectTagExistById(tag_id);
        if (Objects.isNull(tagMark)){
            return MsgRespond.fail("此分类标签不存在，请重新指定");
        }
        // 编辑标签
        tagMapper.updateTagNameByDeptId(tagName, tag_id);
        return MsgRespond.success("已成功编辑此分类标签");
    }

    /**
     * 删除指定资源分类标签
     * @param tagId tagId
     * @return 根据处理结果返回消息提示
     * by organwalk 2023-10-21
     */
    @Override
    public MsgRespond deleteTagByTagId(Integer tagId) {
        // 检查指定的分类标签是否存在
        Integer tagMark = tagMapper.selectTagExistById(tagId);
        if (Objects.isNull(tagMark)){
            return MsgRespond.fail("此分类标签不存在，无需删除");
        }
        // 检查此标签下是否具有资源
        Integer sumMark = resourceNormalMapper.selectResourceNormalSumByTagId(tagId);
        if (sumMark != 0){
            return MsgRespond.fail("此分类标签下存在资源，无法删除。请在清除此分类标签下的资源后重试");
        }
        // 删除标签
        tagMapper.deleteTagByTagId(tagId);
        return MsgRespond.success("已成功删除此分类标签");
    }

    /**
     * 查看指定部门下的资源分类标签列表具体实现
     * @param deptId 部门ID
     * @return 获取标签列表
     * by organwalk 2023-10-21
     */
    @Override
    public DataRespond getTagListByDeptId(Integer deptId) {
        // 检查部门是否存在
        JSONObject deptInfo = deptClient.getDeptExistStatus(deptId);
        if (Objects.equals(deptInfo.getInteger("code"), 5005)){
            return new DataFailRespond(deptInfo.getString("msg"));
        }
        // 获取标签列表
        List<TagRespond> tagList = tagMapper.selectTagListByDeptId(deptId);
        return tagList.isEmpty()
                ? new DataFailRespond("当前部门下标签列表为空")
                : new DataSuccessRespond("已成功获取此部门下标签列表", tagList);
    }
}
