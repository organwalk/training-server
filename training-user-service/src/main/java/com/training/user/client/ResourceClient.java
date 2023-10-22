package com.training.user.client;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

/**
 * 获取资源服务内部接口
 * by organwalk 2023-10-22
 */
@HttpExchange("http://localhost:8183/api/resource")
public interface ResourceClient {
    // 查看指定部门下的资源分类标签列表
    @GetExchange("/v1/file/normal/up/{up_id}/{page_size}/{offset}")
    JSONObject getTagListByDeptId(@PathVariable Integer up_id, @PathVariable Integer page_size, @PathVariable Integer offset);
}
