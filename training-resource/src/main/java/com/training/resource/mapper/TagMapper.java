package com.training.resource.mapper;

import com.training.resource.entity.request.TagReq;
import com.training.resource.entity.respond.TagRespond;
import com.training.resource.entity.table.ResourceTagTable;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * t_resource_tag表的mapper接口
 * by organwalk 2023-10-21
 */
@Mapper
public interface TagMapper {

    // 插入一条创建tag的记录
    @Insert("insert into t_resource_tag(tag_name, dept_id) values (#{req.tag_name}, #{req.dept_id})")
    void insertTag(@Param("req") TagReq req);

    // 根据部门ID和标签名称检查标签是存在
    @Select("select tag_name from t_resource_tag where dept_id = #{deptId} and tag_name = #{tagName}")
    String selectTagExistByDeptIdAndTagName(Integer deptId, String tagName);

    // 根据标签ID检查标签是否存在
    @Select("select id from t_resource_tag where id = #{tagId}")
    Integer selectTagExistById(Integer tagId);

    // 编辑指定资源分类标签
    @Update("update t_resource_tag set tag_name = #{tagName} where id = #{tagId}")
    void updateTagNameByDeptId(@Param("tagName") String tagName, @Param("tagId") Integer tagId);

    // 删除指定资源分类标签
    @Delete("delete from t_resource_tag where id = #{tagId}")
    void deleteTagByTagId(Integer tagId);

    // 根据部门ID获取指定资源分类标签列表
    @Select("select id, tag_name from t_resource_tag where dept_id = #{dept_id}")
    List<TagRespond> selectTagListByDeptId(Integer deptId);

    // 根据标签ID获取指定分类标签
    @Select("select id, tag_name, dept_id from t_resource_tag where id = #{tagId}")
    @Results(value = {
            @Result(id = true, column = "id", property = "id"),
            @Result(column = "tag_name", property = "tag_name"),
            @Result(column = "dept_id", property = "dept_id")
    })
    ResourceTagTable selectTagInfoByTagId(Integer tagId);
}
