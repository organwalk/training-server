package com.training.department.mapper;

import com.training.department.entity.request.MembersReq;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * t_dept_worker表mapper接口
 * by organwalk 2023-10-20
 */
@Mapper
public interface DeptWorkerMapper {

    // 插入一条部门员工记录
    @Insert("insert ignore into t_dept_worker (dept_id, uid) values (#{deptId}, #{uid})")
    void insertDeptWork(@Param("deptId") Integer deptId, @Param("uid") Integer uid);

    // 检查员工是否已经在其他部门
    @Select("select id from t_dept_worker where uid = #{uid}")
    Integer selectWorkerExistByUid(@Param("uid") Integer uid);

    // 检查指定部门下员工是否已经存在
    @Select("select id from t_dept_worker where dept_id = #{deptId} and uid = #{uid}")
    Integer selectWorkerExistByDept(@Param("deptId") Integer deptId, @Param("uid") Integer uid);

    // 删除指定部门下的所有员工
    @Delete("delete from t_dept_worker where dept_id = #{deptId}")
    void deleteAllWorkerByDeptId(Integer deptId);

    // 获取指定部门下的成员列表总量
    @Select("select COUNT(id) from t_dept_worker where dept_id = #{deptId}")
    Integer selectUidListSumByDeptId(Integer deptId);

    // 获取指定部门下的成员列表
    @Select("select uid from t_dept_worker where dept_id = #{deptId} limit #{pageSize} offset #{offset}")
    List<Integer> selectUidListByDeptId(@Param("deptId") Integer deptId,
                                        @Param("pageSize") Integer pageSize,
                                        @Param("offset") Integer offset);

    // 根据部门ID和成员ID为部门添加成员
    @Insert("insert into t_dept_worker(dept_id, uid) values (#{req.dept_id}, #{req.uid})")
    void insertMember(@Param("req") MembersReq req);

    // 删除指定部门下的指定员工
    @Delete("delete from t_dept_worker where dept_id = #{req.dept_id} and uid = #{req.uid}")
    void deleteWorkerByDeptId(@Param("req") MembersReq req);

    // 根据成员ID获取部门ID
    @Select("select dept_id from t_dept_worker where uid = #{uid}")
    Integer selectDeptIdByUid(Integer uid);
}
