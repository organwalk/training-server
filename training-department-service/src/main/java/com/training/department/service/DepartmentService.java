package com.training.department.service;

import com.training.common.entity.DataRespond;
import com.training.common.entity.MsgRespond;
import com.training.department.entity.request.DeptReq;
import com.training.department.entity.request.MembersReq;
import com.training.department.entity.table.DeptTable;

import java.util.List;

/**
 * 定义部门管理业务接口
 * by organwalk 2023-10-20
 */
public interface DepartmentService {

    // 创建部门
    MsgRespond createDept(DeptReq req);

    // 获取部门列表
    DataRespond getDeptList(Integer pageSize, Integer offset);

    // 编辑指定部门
    MsgRespond editDeptInfo(Integer deptId, DeptReq req);

    // 获取指定部门下的成员列表
    DataRespond getDeptMemberList(Integer deptId, Integer pageSize, Integer offset);

    // 添加指定员工至指定部门
    MsgRespond addMemberToDept(MembersReq req);

    // 删除指定部门下的指定员工
    MsgRespond deleteMember(MembersReq req);

    // 获取指定员工的部门ID
    DataRespond getDeptIdByUid(Integer uid);

    // 获取部门存在状态
    DataRespond getDeptExistStatus(Integer deptId);

    // 获取指定部门信息
    DataRespond getDeptInfoByDeptId(Integer deptId);

    // 根据部门ID列表获取部门信息列表
    DataRespond getDeptListByDeptList(List<Integer> deptIdList);

    // 根据部门名称模糊查询部门
    DataRespond getDeptListByKeyword(String keyword, Integer pageSize, Integer offset);
}
