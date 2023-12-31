package com.training.department.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.training.common.entity.DataRespond;
import com.training.common.entity.MsgRespond;
import com.training.common.entity.req.DeptListReq;
import com.training.department.entity.request.DeptReq;
import com.training.department.entity.request.MembersReq;
import com.training.department.exceptions.GlobeBlockException;
import com.training.department.service.DepartmentService;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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

    // 获取指定部门下的成员列表
    @GetMapping("/v1/department/{dept_id}/{page_size}/{offset}")
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
    @SentinelResource(value = "deleteMember",
            blockHandlerClass = GlobeBlockException.class,
            blockHandler = "blockedDeleteMember")
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
    @SentinelResource(value = "getDeptId",
            blockHandlerClass = GlobeBlockException.class,
            blockHandler = "blockedGetDeptId")
    public DataRespond getDeptId(@PathVariable Integer uid) {
        return deptService.getDeptIdByUid(uid);
    }

    // 获取部门存在状态
    @GetMapping("/v1/department/status/{dept_id}")
    @SentinelResource(value = "getDeptExistStatus",
            blockHandlerClass = GlobeBlockException.class,
            blockHandler = "blockedGetDeptExistStatus")
    public DataRespond getDeptExistStatus(@PathVariable Integer dept_id) {
        return deptService.getDeptExistStatus(dept_id);
    }

    // 获取指定部门信息
    @GetMapping("/v1/department/info/{dept_id}")
    @SentinelResource(value = "getDeptInfo",
            blockHandlerClass = GlobeBlockException.class,
            blockHandler = "blockedGetDeptInfo")
    public DataRespond getDeptInfo(@PathVariable
                                   @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "dept_id必须为纯数字字段")
                                   Integer dept_id) {
        return deptService.getDeptInfoByDeptId(dept_id);
    }

    // 根据部门ID列表获取部门列表
    @GetMapping("/v1/info/list")
    @SentinelResource(value = "getDeptList",
            blockHandlerClass = GlobeBlockException.class,
            blockHandler = "blockedGetDeptList")
    public DataRespond getDeptList(@RequestBody DeptListReq req) {
        return deptService.getDeptListByDeptList(req.getDeptIdList());
    }

    // 模糊查询部门
    @GetMapping("/v1/department/keyword/{keyword}/{page_size}/{offset}")
    public DataRespond getDeptByKeyword(@PathVariable
                                        @NotBlank(message = "keyword不能为空")
                                        String keyword, @PathVariable
                                        @Min(value = 1, message = "page_size必须为大于1的整数")
                                        @Digits(integer = Integer.MAX_VALUE, fraction = 0)
                                        Integer page_size,
                                        @PathVariable
                                        @Min(value = 0, message = "offset必须为大于或等于0的整数")
                                        @Digits(integer = Integer.MAX_VALUE, fraction = 0)
                                        Integer offset) {
        return deptService.getDeptListByKeyword(keyword, page_size, offset);
    }
}
