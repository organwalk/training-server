package com.training.resource.mapper;

import com.training.resource.entity.request.ResourceNormalReq;
import com.training.resource.entity.respond.ResourceNormalAllListRespond;
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
    @Select("select id, resource_name, up_id, up_datetime from t_resource_normal where dept_id = #{dept_id} and tag_id = #{tag_id} ORDER BY id DESC limit #{pageSize} offset #{offset}")
    @Results(id = "resource_normal_list", value = {
            @Result(id = true, column = "id", property = "id"),
            @Result(column = "resource_name", property = "resource_name"),
            @Result(column = "up_id", property = "up_id"),
            @Result(column = "up_datetime", property = "up_datetime")
    })
    List<ResourceNormalRespond> selectResourceListByDeptIdAndTagId(@Param("dept_id") Integer deptId,
                                                                   @Param("tag_id") Integer tagId,
                                                                   @Param("pageSize") Integer pageSize,
                                                                   @Param("offset") Integer offset);

    // 获取所有上传资源列表总数
    @Select("select count(id) from t_resource_normal")
    Integer selectResourceListSum();
    // 分页获取所有上传资源列表
    @Select("select id, resource_name, up_id, up_datetime, dept_id, tag_id from t_resource_normal  ORDER BY id DESC limit #{pageSize} offset #{offset}")
    @Results(id = "resource_normal_all", value = {
            @Result(id = true, column = "id", property = "id"),
            @Result(column = "resource_name", property = "resource_name"),
            @Result(column = "up_id", property = "up_id"),
            @Result(column = "up_datetime", property = "up_datetime"),
            @Result(column = "dept_id", property = "dept_id"),
            @Result(column = "tag_id", property = "tag_id"),
            @Result(column = "tag_id", property = "tagInfo", javaType = ResourceTagTable.class,
            one = @One(select = "com.training.resource.mapper.TagMapper.selectTagInfoByTagId"))
    })
    List<ResourceNormalAllListRespond> selectResourceList(@Param("pageSize") Integer pageSize,
                                                          @Param("offset") Integer offset);

    // 根据ID获取文件下载路径
    @Select("select resource_path from t_resource_normal where id = #{rid}")
    String selectPathByRid(Integer rid);
    // 根据rid获取文件名
    @Select("select resource_name from t_resource_normal where id = #{rid}")
    String selectFileNameByRid(Integer rid);

    // 根据资源ID获取资源详情
    @Select("select resource_name, dept_id, tag_id, up_id, up_datetime from t_resource_normal where id = #{rid}")
    @Results(value = {
            @Result(column = "resource_name", property = "resource_name"),
            @Result(column = "dept_id", property = "dept_id"),
            @Result(column = "tag_id", property = "tag_id"),
            @Result(column = "tag_id", property = "tagInfo", javaType = ResourceTagTable.class,
                    one = @One(select = "com.training.resource.mapper.TagMapper.selectTagInfoByTagId")),
            @Result(column = "up_id", property = "up_id"),
            @Result(column = "up_datetime", property = "up_datetime"),
    })
    ResourceNormalDetailRespond selectResourceNormalDetailByRidAnUpId(Integer rid);

    // 根据id获取指定的资源文件路径
    @Select("select resource_path from t_resource_normal where id = #{rid}")
    String selectResourcePathByRid(Integer rid);

    // 根据rid编辑指定的资源文件
    @Update("<script>" +
            "update t_resource_normal set dept_id = #{req.dept_id}, tag_id = #{req.tag_id}, resource_name = #{req.resource_name}, up_datetime = #{upDatetime} " +
            "<if test='resourcePath != null'>" +
            ", resource_path = #{resourcePath} " +
            "</if>" +
            "where id = #{rid}" +
            "</script>")
    void updateResourceNormalInfoByRid(@Param("req") ResourceNormalReq req,
                                       @Param("resourcePath") String resourcePath,
                                       @Param("upDatetime") String upDatetime,
                                       @Param("rid") Integer rid);

    // 根据rid删除指定的资源文件
    @Select("delete from t_resource_normal where id = #{rid}")
    void deleteResourceNormalByRid(Integer rid);

    // 根据tag_id获取资源总数
    @Select("select count(id) from t_resource_normal where tag_id = #{tagId}")
    Integer selectResourceNormalSumByTagId(Integer tagId);

    // 根据up_id获取其上传资源列表总数
    @Select("select count(id) from t_resource_normal where up_id = #{upId}")
    Integer selectResourceListSumByUpId(Integer upId);

    // 根据up_id获取资源文件列表
    @Select("select id, resource_name, up_id, up_datetime from t_resource_normal where up_id = #{upId} limit #{pageSize} offset #{offset}")
    @ResultMap("resource_normal_list")
    List<ResourceNormalRespond> selectResourceListByUpId(@Param("upId") Integer upId,
                                                         @Param("pageSize") Integer pageSize,
                                                         @Param("offset") Integer offset);
    // 模糊查询资源信息总数
    @Select({"<script>",
            "select count(id) ",
            "from t_resource_normal ",
            "where 1=1",
            "<if test='dept_id != null and tag_id != null'>" ,
            "and dept_id = #{dept_id} and tag_id = #{tag_id}",
            "</if>",
            "and resource_name like concat('%', #{keyword}, '%')",
            "ORDER BY id DESC ",
            "limit #{pageSize} offset #{offset}",
            "</script>"})
    Integer selectResourceListByKeywordSum(@Param("dept_id") Integer deptId,
                                           @Param("tag_id") Integer tagId,
                                           @Param("keyword") String keyword,
                                           @Param("pageSize") Integer pageSize,
                                           @Param("offset") Integer offset);
    // 模糊查询资源信息
    @Select({"<script>",
            "select id, resource_name, up_id, up_datetime, dept_id, tag_id ",
            "from t_resource_normal ",
            "where 1=1",
            "<if test='dept_id != null and tag_id != null'>" ,
            "and dept_id = #{dept_id} and tag_id = #{tag_id}",
            "</if>",
            "and resource_name like concat('%', #{keyword}, '%')",
            "ORDER BY id DESC ",
            "limit #{pageSize} offset #{offset}",
            "</script>"})
    @ResultMap(value = "resource_normal_all")
    List<ResourceNormalAllListRespond> selectResourceListByKeyword(@Param("dept_id") Integer deptId,
                                                                   @Param("tag_id") Integer tagId,
                                                                   @Param("keyword") String keyword,
                                                                   @Param("pageSize") Integer pageSize,
                                                                   @Param("offset") Integer offset);
}
