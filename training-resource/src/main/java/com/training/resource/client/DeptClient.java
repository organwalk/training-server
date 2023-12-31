package com.training.resource.client;

import com.alibaba.fastjson.JSONObject;
import com.training.common.entity.req.DeptListReq;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

/**
 * 获取部门管理服务内部接口
 * by organwalk 2023-10-21
 */
@HttpExchange("http://localhost:8182/api/dept")
public interface DeptClient {

    // 获取部门存在状态
    @GetExchange("/v1/department/status/{dept_id}")
    JSONObject getDeptExistStatus(@PathVariable Integer dept_id);

    // 获取指定部门信息
    @GetExchange("/v1/department/info/{dept_id}")
    JSONObject getDeptInfo(@PathVariable Integer dept_id);

    // 根据部门ID列表获取部门信息列表
    @GetExchange("/v1/info/list")
    JSONObject getDeptInfoByDeptIdList(@RequestBody DeptListReq req);
}
