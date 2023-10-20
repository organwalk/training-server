package com.training.department.controller;

import com.training.common.entity.DataRespond;
import com.training.common.entity.MsgRespond;
import com.training.department.entity.request.DeptReq;
import com.training.department.entity.request.MembersReq;
import com.training.department.service.DepartmentService;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 定义部门管理服务接口
 * by organwalk 2023-10-20
 */
@RestController
@RequestMapping("/api/dept")
@AllArgsConstructor
@Validated
public class DeptServiceController {

    private final DepartmentService deptService;

    // 创建部门
    @PostMapping("/v3/department")
    public MsgRespond createDept(@Validated @RequestBody DeptReq req) {
        return deptService.createDept(req);
    }

    // 获取部门列表
    @GetMapping("/v1/department/{page_size}/{offset}")
    public DataRespond getDeptList(@PathVariable
                                   @Min(value = 1, message = "page_size必须为大于1的整数")
                                   @Digits(integer = Integer.MAX_VALUE, fraction = 0)
                                   Integer page_size,
                                   @PathVariable
                                   @Min(value = 0, message = "offset必须为大于或等于0的整数")
                                   @Digits(integer = Integer.MAX_VALUE, fraction = 0)
                                   Integer offset) {
        return deptService.getDeptList(page_size, offset);
    }

    // 编辑指定部门
    @PutMapping("/v3/department/{dept_id}")
    public MsgRespond editDeptInfo(@PathVariable
                                   @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "dept_id必须为纯数字字段")
                                   Integer dept_id,
                                   @Validated @RequestBody DeptReq req) {
        return deptService.editDeptInfo(dept_id, req);
    }

    // 删除指定部门
    @DeleteMapping("/v3/department/{dept_id}")
    public MsgRespond deleteDept(@PathVariable
                                 @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "dept_id必须为纯数字字段")
                                 Integer dept_id) {
        return deptService.deleteDept(dept_id);
    }

    // 获取指定部门下的成员列表
    @GetMapping("/v3/department/{dept_id}/{page_size}/{offset}")
    public DataRespond getDeptMemberList(@PathVariable
                                         @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "dept_id必须为纯数字字段")
                                         Integer dept_id,
                                         @PathVariable
                                         @Min(value = 1, message = "page_size必须为大于1的整数")
                                         @Digits(integer = Integer.MAX_VALUE, fraction = 0)
                                         Integer page_size,
                                         @PathVariable
                                         @Min(value = 0, message = "offset必须为大于或等于0的整数")
                                         @Digits(integer = Integer.MAX_VALUE, fraction = 0)
                                         Integer offset) {
        return deptService.getDeptMemberList(dept_id, page_size, offset);
    }

    // 添加指定员工至指定部门
    @PostMapping("/v3/department/worker")
    public MsgRespond addMember(@Validated @RequestBody MembersReq req) {
        return deptService.addMemberToDept(req);
    }

    // 删除指定部门下的指定员工
    @DeleteMapping("/v3/worker/{dept_id}/{uid}")
    public MsgRespond deleteMember(@PathVariable
                                   @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "dept_id必须为纯数字字段")
                                   Integer dept_id,
                                   @PathVariable
                                   @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "dept_id必须为纯数字字段")
                                   Integer uid) {
        return deptService.deleteMember(new MembersReq(dept_id, uid));
    }

    // 获取指定员工的部门ID
    @GetMapping("/v1/department/{uid}")
    public Integer getDeptId(@PathVariable Integer uid){
        return  deptService.getDeptIdByUid(uid);
    }
}
