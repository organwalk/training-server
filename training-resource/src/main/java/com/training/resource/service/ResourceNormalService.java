package com.training.resource.service;

import com.training.common.entity.DataRespond;
import com.training.common.entity.MsgRespond;
import com.training.resource.entity.request.ResourceNormalReq;
import org.springframework.http.ResponseEntity;

/**
 * 定义普通资源相关业务
 * by organwalk 2023-10-21
 */
public interface ResourceNormalService {
    // 上传资源文件
    MsgRespond uploadResourceNormalFile(ResourceNormalReq req);
    // 获取上传资源列表
    DataRespond getResourceNormalList(Integer deptId, Integer tagId, Integer pageSize, Integer offset);
    // 下载指定资源文件
    ResponseEntity<?> downloadResourceNormalFile(Integer rid);
    // 获取指定资源文件详情
    DataRespond getResourceNormalDetail(Integer rid);
    // 编辑指定资源文件信息
    MsgRespond editResourceNormalInfo(Integer rid, ResourceNormalReq req, String username, String auth);
    // 删除指定资源文件
    MsgRespond deleteResourceNormal(Integer rid, Integer uid, String username, String auth);
    // 获取指定用户上传的资源文件列表
    DataRespond getResourceNormalListByUpId(Integer upId, Integer pageSize, Integer offset);
}
