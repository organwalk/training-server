package com.training.user.client;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

/**
 * 获取部门管理服务内部接口
 * by organwalk 2023-10-20
 */
@HttpExchange("http://localhost:8182/api/dept")
public interface DeptClient {

    // 获取指定员工的部门ID
    @GetExchange("/v1/department/{uid}")
    Integer getDeptIdByUid(@PathVariable Integer uid);
    // 删除部门成员
    @DeleteExchange("/v3/worker/{dept_id}/{uid}")
    JSONObject deleteMember(@PathVariable Integer dept_id, @PathVariable Integer uid);
}
