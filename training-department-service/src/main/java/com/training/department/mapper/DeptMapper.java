package com.training.department.mapper;

import com.training.department.entity.request.DeptReq;
import com.training.department.entity.table.DeptTable;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * t_dept表mapper接口
 * by organwalk 2023-10-20
 */
@Mapper
public interface DeptMapper {

    // 插入一条部门记录
    @Insert("insert into t_dept (dept_name, head_id) values (#{dept_name}, #{head_id})")
    void insertDept(DeptReq req);

    // 检查部门名重复数量
    @Select("select id from t_dept where dept_name = #{deptName}")
    Integer selectDeptNameExist(String deptName);

    // 根据负责人ID获取部门ID
    @Select("select id from t_dept where head_id = #{headId}")
    Integer selectDeptIdByHeadId(Integer headId);

    // 获取所有部门列表总数
    @Select("select count(id) from t_dept")
    Integer selectDeptListSum();

    // 获取所有部门列表
    @Select("select * from t_dept limit #{pageSize} offset #{offset}")
    List<DeptTable> selectDeptList(@Param("pageSize") Integer pageSize, @Param("offset") Integer offset);

    // 根据部门ID检查部门是否存在
    @Select("select id from t_dept where id = #{deptId}")
    Integer selectDeptExist(Integer deptId);

    // 根据部门ID编辑部门
    @Update("update t_dept set dept_name = #{req.dept_name}, head_id = #{req.head_id} where id = #{dept_id}")
    void updateDeptInfoByDeptId(@Param("dept_id") Integer deptId, @Param("req") DeptReq req);


    // 根据部门ID删除指定部门
    @Delete("delete from t_dept where id = #{deptId}")
    void deleteDeptByDeptId(Integer deptId);

    // 检查成员是否是负责人身份
    @Select("select id from t_dept where head_id = #{uid}")
    Integer selectIdentityStatusByUid(Integer uid);

    // 根据部门ID获取指定部门信息
    @Select("select * from t_dept where id = #{deptId}")
    DeptTable selectDeptInfoByDeptId(Integer deptId);
}
