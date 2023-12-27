package com.training.department.exceptions;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.training.common.entity.DataFailRespond;
import com.training.common.entity.DataRespond;
import com.training.common.entity.req.DeptListReq;

public class GlobeBlockException {
    public static DataRespond blockedGetDeptInfo(Integer dept_id, BlockException e){
        return new DataFailRespond("部门管理服务异常，无法正常获取部门信息");
    }

    public static DataRespond blockedGetDeptId(Integer uid, BlockException e){
        return new DataFailRespond("部门管理服务异常，无法正常获取部门ID");
    }
    public static DataRespond blockedGetDeptExistStatus(Integer dept_id, BlockException e){
        return new DataFailRespond("部门管理服务异常，无法正常获取部门存在状态");
    }
    public static DataRespond blockedGetDeptList(DeptListReq req, BlockException e){
        return new DataFailRespond("部门管理服务异常，无法正常获取部门列表");
    }
    public static DataRespond blockedDeleteMember(Integer dept_id, Integer uid){
        return new DataFailRespond("部门管理服务异常，无法正常移除员工");
    }
}
