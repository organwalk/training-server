package com.training.mapper;

import com.training.entity.request.EditAccountReq;
import com.training.entity.table.AuthTable;
import com.training.entity.table.UserTable;
import org.apache.ibatis.annotations.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 定义t_user表的Mapper接口
 */
@Mapper
public interface UserMapper {

    // 获取权限列表
    @Select("select id, auth_name from t_auth")
    List<AuthTable> selectAuthList();

    // 插入一条账号记录
    @Insert("insert into t_user (username, password, real_name, mobile, auth_id) values (#{username}, #{password}, #{realName}, #{mobile}, #{authId})")
    Integer insertUser(UserTable userTable);

    // 检查username是否存在于表中
    @Select("select COUNT(id) from t_user where username = #{username}")
    Integer selectExistUsername(String username);

    // 获取用户加密后的密码
    @Select("select password from t_user where username = #{username}")
    String selectPasswordByUsername(String username);

    // 获取用户认证信息
    @Select("select * from t_user where username = #{username}")
    UserTable selectAuthInfoByUsername(String username);

    // 根据权限ID获取权限
    @Select("select * from t_auth where id = #{authId}")
    AuthTable selectAuthNameById(Integer authId);

    // 根据类别获取用户列表总数
    @Select("<script>" +
            "select count(id) from t_user " +
            "<if test='authId != 4'>" +
            "where auth_id = #{authId}" +
            "</if>" +
            "</script>")
    Integer selectUserAccountSumByAuthId(Integer authId);

    // 根据类别获取用户列表
    @Select({
            "<script>",
            "select * from t_user",
            "<if test='authId != 4'>",
            "where auth_id = #{authId}",
            "</if>",
            "limit #{pageSize} offset #{offset}",
            "</script>"
    })
    @Results(id = "UserTable", value = {
            @Result(id = true, column = "id", property = "id"),
            @Result(column = "username", property = "username"),
            @Result(column = "password", property = "password"),
            @Result(column = "real_name", property = "realName"),
            @Result(column = "mobile", property = "mobile"),
            @Result(column = "extra", property = "extra"),
            @Result(column = "auth_id", property = "auth", javaType = AuthTable.class,
                    one = @One(select = "com.training.mapper.UserMapper.selectAuthNameById"))

    })
    List<UserTable> selectUserAccountByAuthId(@Param("authId") Integer authId,
                                              @Param("pageSize") Integer pageSize,
                                              @Param("offset") Integer offset
    );

    // 根据用户ID获取账号信息
    @Select("select * from t_user where id = #{uid}")
    @ResultMap("UserTable")
    UserTable selectUserAccountByUid(Integer uid);

    // 根据用户ID编辑账号信息
    @Transactional
    @Update("update t_user set real_name = #{req.real_name}, password = #{req.password}, mobile = #{req.mobile}, auth_id = #{req.auth_id} where id = #{uid}")
    Integer updateUserAccountInfoByUid(@Param("uid") Integer uid, @Param("req") EditAccountReq req);

    // 根据用户ID删除用户
    @Delete("delete from t_user where id = #{uid}")
    Integer deleteUserAccountByUid(Integer uid);

}
