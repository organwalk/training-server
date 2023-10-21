package com.training.resource.mapper;

import com.training.resource.entity.respond.ResourceNormalDetailRespond;
import com.training.resource.entity.respond.ResourceNormalRespond;
import com.training.resource.entity.table.ResourceNormalTable;
import com.training.resource.entity.table.ResourceTagTable;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ResourceNormalMapper {

    // 插入一条文件记录
    @Insert("insert into t_resource_normal(resource_name, resource_path, dept_id, tag_id, up_id, up_datetime) " +
            "values (#{obj.resourceName}, #{obj.resourcePath}, #{obj.deptId}, #{obj.tagId}, #{obj.upId}, #{obj.upDatetime})")
    void insertResourceNormal(@Param("obj") ResourceNormalTable obj);

    // 获取指定部门、分类标签下上传资源列表总数
    @Select("select count(id) from t_resource_normal where dept_id = #{dept_id} and tag_id = #{tag_id}")
    Integer selectResourceListSumByDeptIdAndTagId(@Param("dept_id") Integer deptId, @Param("tag_id") Integer tagId);

    // 分页获取指定部门、分类标签下的上传资源列表
    @Select("select id, resource_name, up_id, up_datetime from t_resource_normal where dept_id = #{dept_id} and tag_id = #{tag_id} limit #{pageSize} offset #{offset}")
    @Results(value = {
            @Result(id = true, column = "id", property = "id"),
            @Result(column = "resource_name", property = "resource_name"),
            @Result(column = "up_id", property = "up_id"),
            @Result(column = "up_datetime", property = "up_datetime")
    })
    List<ResourceNormalRespond> selectResourceListByDeptIdAndTagId(@Param("dept_id") Integer deptId,
                                                                   @Param("tag_id") Integer tagId,
                                                                   @Param("pageSize") Integer pageSize,
                                                                   @Param("offset") Integer offset);

    // 根据ID获取文件下载路径
    @Select("select resource_path from t_resource_normal where id = #{rid}")
    String selectPathByRid(Integer rid);

    // 根据资源ID获取资源详情
    @Select("select resource_name, dept_id, tag_id, up_id, up_datetime from t_resource_normal where id = #{rid}")
    @Results(value = {
            @Result(column = "resource_name", property = "resource_name"),
            @Result(column = "dept_id", property = "dept_id"),
            @Result(column = "tag_id", property = "tagInfo", javaType = ResourceTagTable.class,
                    one = @One(select = "com.training.resource.mapper.TagMapper.selectTagInfoByTagId")),
            @Result(column = "up_id", property = "up_id"),
            @Result(column = "up_datetime", property = "up_datetime"),
    })
    ResourceNormalDetailRespond selectResourceNormalDetailByRidAnUpId(Integer rid);
}
