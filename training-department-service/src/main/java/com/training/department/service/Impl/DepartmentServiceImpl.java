package com.training.department.service.Impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.training.common.entity.*;
import com.training.common.entity.req.UserInfoListReq;
import com.training.department.client.ResourceClient;
import com.training.department.client.UserClient;
import com.training.department.entity.request.DeptReq;
import com.training.department.entity.request.MembersReq;
import com.training.department.entity.result.MembersInfo;
import com.training.department.entity.table.DeptTable;
import com.training.department.mapper.DeptMapper;
import com.training.department.mapper.DeptWorkerMapper;
import com.training.department.service.DepartmentService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * 部门管理业务具体实现
 * by organwalk 2023-10-20
 */
@Service
@AllArgsConstructor
@Transactional
public class DepartmentServiceImpl implements DepartmentService {

    private final UserClient userClient;
    private final ResourceClient resourceClient;
    private final DeptMapper deptMapper;
    private final DeptWorkerMapper deptWorkerMapper;

    /**
     * 创建部门的具体实现
     * @param req 请求实体
     * @return 根据处理结果返回对应消息
     * by organwalk 2023-10-20
     */
    @Override
    public MsgRespond createDept(DeptReq req) {
        // 对请求实体进行检查
        String checkResult = checkInfoForCreateOrUpdateDept(null, req);
        if (!checkResult.isBlank()){
            return MsgRespond.fail(checkResult);
        }
        Integer workerMark = deptWorkerMapper.selectWorkerExistByUid(req.getHead_id());
        // 检查员工是否空闲
        if (Objects.nonNull(workerMark)){
            return MsgRespond.fail("此员工已在其他部门任职，无法担任此部门负责人");
        }
        // 创建部门
        deptMapper.insertDept(req);
        // 将部门负责人纳入部门成员
        Integer deptId = deptMapper.selectDeptIdByHeadId(req.getHead_id());
        deptWorkerMapper.insertDeptWork(deptId, req.getHead_id());
        return MsgRespond.success("已成功创建此部门");
    }

    /**
     * 获取部门列表具体实现
     * @param pageSize 读取记录数
     * @param offset 从第几条记录开始读取
     * @return 部门列表，若为空，则返回提示消息
     * by organwalk 2023-10-20
     */
    @Override
    public DataRespond getDeptList(Integer pageSize, Integer offset) {
        Integer sumMark = deptMapper.selectDeptListSum();
        if (sumMark == 0){
            return new DataFailRespond("部门列表为空，请先创建部门");
        }
        return new DataPagingSuccessRespond("已成功获取部门列表", sumMark, deptMapper.selectDeptList(pageSize, offset));
    }

    /**
     * 编辑指定部门具体实现
     * @param deptId 部门ID
     * @param req 请求实体，包含编辑信息
     * @return 根据处理结果返回消息提示
     * by organwalk 2023-10-20
     */
    @Override
    public MsgRespond editDeptInfo(Integer deptId, DeptReq req) {
        // 检查被编辑的部门是否存在
        Integer deptMark = deptMapper.selectDeptExist(deptId);
        if (Objects.isNull(deptMark)){
            return MsgRespond.fail("该部门不存在，无法进行编辑");
        }
        // 对请求实体进行检查
        String checkResult = checkInfoForCreateOrUpdateDept(deptId, req);
        if (!checkResult.isBlank()){
            return MsgRespond.fail(checkResult);
        }
        // 进行部门编辑
        deptMapper.updateDeptInfoByDeptId(deptId, req);
        // 如果是新成员，则将新的部门负责人纳入部门成员
        Integer idMark = deptWorkerMapper.selectWorkerExistByDept(deptId, req.getHead_id());
        if (Objects.isNull(idMark)){
            deptWorkerMapper.insertDeptWork(deptId, req.getHead_id());
        }
        return MsgRespond.success("已成功编辑此部门");
    }

    /**
     * 删除指定部门具体实现
     * @param deptId 部门ID
     * @return 根据处理结果返回提示消息
     */
    @Override
    public MsgRespond deleteDept(Integer deptId) {
        // 检查被删除的部门是否存在
        Integer deptMark = deptMapper.selectDeptExist(deptId);
        if (Objects.isNull(deptMark)){
            return MsgRespond.fail("该部门不存在，无法删除");
        }
        // 检查此部门是否设置了部门标签
        Integer codeMark = resourceClient.getTagListByDeptId(deptId).getInteger("code");
        if (Objects.equals(codeMark, 2002)){
            return MsgRespond.fail("该部门已定义资源分类标签，无法删除。请在删除资源分类标签后重试");
        }
        // 先删除指定部门下的员工
        deptWorkerMapper.deleteAllWorkerByDeptId(deptId);
        // 再删除指定部门
        deptMapper.deleteDeptByDeptId(deptId);
        return MsgRespond.success("已成功删除此部门");
    }

    /**
     * 获取指定部门下的成员列表具体实现
     * @param deptId 部门ID
     * @return 返回成员列表
     */
    @Override
    public DataRespond getDeptMemberList(Integer deptId, Integer pageSize, Integer offset) {
        // 检查获取的部门是否存在
        Integer deptMark = deptMapper.selectDeptExist(deptId);
        if (Objects.isNull(deptMark)){
            return new DataFailRespond("该部门不存在，无法获取成员列表");
        }
        // 获取成员总量
        Integer sumMark = deptWorkerMapper.selectUidListSumByDeptId(deptId);
        // 获取UID列表
        List<Integer> uidList = deptWorkerMapper.selectUidListByDeptId(deptId, pageSize, offset);
        JSONArray userInfoList = userClient.getUserInfoByUidList(new UserInfoListReq(uidList));
        List<MembersInfo> membersInfoList = JSON.parseArray(userInfoList.toJSONString(), MembersInfo.class);
        return new DataPagingSuccessRespond("已成功获取当前部门下成员列表", sumMark, membersInfoList);
    }

    /**
     * 添加指定员工至指定部门具体实现
     * @param req 请求实体，包含部门ID和成员ID
     * @return 根据处理结果返回提示信息
     * by organwalk 2023-10-20
     */
    @Override
    public MsgRespond addMemberToDept(MembersReq req) {
        Integer deptId = req.getDept_id();
        Integer uid = req.getUid();
        // 对请求实体进行检查
        String checkInfo = checkInfoForAddOrDeleteMember(deptId, uid);
        if (!checkInfo.isBlank()){
            return MsgRespond.fail(checkInfo);
        }
        // 检查员工是否已经在某个部门
        Integer memberMark = deptWorkerMapper.selectWorkerExistByDept(deptId, uid);
        if (Objects.nonNull(memberMark)){
            return MsgRespond.fail("该员工已在此部门，无需添加");
        }
        Integer memberMark2 = deptWorkerMapper.selectWorkerExistByUid(uid);
        if (Objects.nonNull(memberMark2)){
            return MsgRespond.fail("该员工已在其他部门，一个员工只能同时存在于一个部门");
        }
        deptWorkerMapper.insertMember(req);
        return MsgRespond.success("已成功添加员工至此部门");
    }

    /**
     * 删除指定部门下的指定员工具体实现
     * @param req 请求实体，包含部门ID和成员ID
     * @return 根据处理结果返回提示信息
     * by organwalk 2023-10-20
     */
    @Override
    public MsgRespond deleteMember(MembersReq req) {
        Integer deptId = req.getDept_id();
        Integer uid = req.getUid();
        // 对请求实体进行检查
        String checkInfo = checkInfoForAddOrDeleteMember(deptId, uid);
        if (!checkInfo.isBlank()){
            return MsgRespond.fail(checkInfo);
        }
        // 检查指定用户是否是部门负责人
        Integer identityMark = deptMapper.selectIdentityStatusByUid(uid);
        if (Objects.nonNull(identityMark)){
            return MsgRespond.fail("该用户为部门负责人，无法删除。请在更换部门负责人后进行此操作");
        }
        Integer memberMark = deptWorkerMapper.selectWorkerExistByDept(deptId, uid);
        if (Objects.isNull(memberMark)){
            return MsgRespond.fail("该部门下不存在此成员，无需删除");
        }
        // 删除该成员
        deptWorkerMapper.deleteWorkerByDeptId(req);
        return MsgRespond.success("已成功删除此成员");
    }

    /**
     * 定义获取指定员工的部门ID具体实现
     * @param uid 成员ID
     * @return null或者部门ID
     */
    @Override
    public Integer getDeptIdByUid(Integer uid) {
        return deptWorkerMapper.selectDeptIdByUid(uid);
    }

    /**
     * 定义根据部门ID获取部门存在状态的具体实现
     * @param deptId 部门ID
     * @return null或者整数ID
     */
    @Override
    public Integer getDeptExistStatus(Integer deptId) {
        return deptMapper.selectDeptExist(deptId);
    }

    /**
     * 定义获取指定部门信息的具体实现
     * @param deptId 部门ID
     * @return 部门信息或错误提示
     */
    @Override
    public DataRespond getDeptInfoByDeptId(Integer deptId) {
        DeptTable deptTable = deptMapper.selectDeptInfoByDeptId(deptId);
        if (Objects.isNull(deptTable)){
            return new DataFailRespond("此部门不存在，请重新指定");
        }
        return new DataSuccessRespond("已成功获取该部门信息", deptTable);
    }


    /**
     * 定义创建或编辑的部门信息进行通用检查方法
     * @param deptId 部门ID，当进行更新操作检查时传入
     * @param req 创建或编辑请求实体
     * @return 返回错误消息，若检查通过，则返回空字符串
     */
    private String checkInfoForCreateOrUpdateDept(Integer deptId, DeptReq req){
        Integer headId = req.getHead_id();
        // 检查部门名是否已经存在
        Integer idMark = deptMapper.selectDeptNameExist(req.getDept_name());
        if (Objects.nonNull(idMark) && !Objects.equals(deptId, idMark)){
            return "此部门已存在，请重新输入部门名";
        }
        // 检查部门负责人是否存在以及是否是员工
        JSONObject resObject = userClient.getUserAccountByUid(headId);
        if (Objects.equals(resObject.get("code"), 5005)){
            return "该员工不存在，请重新指定部门负责人";
        }
        if (!Objects.equals(resObject.getJSONObject("data").get("authId"), 1)){
            return "此用户非员工身份，请重新指定部门负责人";
        }
        // 检查部门负责人是否已经负责了某部门
        Integer deptIdMark = deptMapper.selectDeptIdByHeadId(req.getHead_id());
        if (deptIdMark != null && !Objects.equals(deptIdMark, idMark)){
            return "该员工已负责其他部门，请重新指派负责人";
        }
        return "";
    }

    /**
     * 定义添加或删除部门成员操作的通用检查方法
     * @param deptId 部门ID
     * @param uid 成员ID
     * @return 返回提示信息，如果检查通过，则为空字符串
     */
    private String checkInfoForAddOrDeleteMember(Integer deptId, Integer uid){
        // 检查指定的部门是否存在
        Integer deptMark = deptMapper.selectDeptExist(deptId);
        if (Objects.isNull(deptMark)){
            return "指定的部门不存在";
        }
        // 检查员工是否存在
        JSONObject resObject = userClient.getUserAccountByUid(uid);
        if (Objects.equals(resObject.get("code"), 5005)){
            return "该员工不存在，请重新指定员工";
        }
        if (!Objects.equals(resObject.getJSONObject("data").get("authId"), 1)){
            return "此用户非员工身份，请重新指定员工";
        }
        return "";
    }
}
