package com.training.plan.client;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
/**
 * 获取部门服务内部接口
 */
@HttpExchange("http://localhost:8182/api/dept")
public interface DeptClient {
    //获取指定部门的信息
    @GetExchange("/v1/department/info/{dept_id}")
    JSONObject getDeptInfoByDeptId(@PathVariable Integer dept_id);
}