package com.training.mapper;

import com.training.entity.table.AuthTable;
import com.training.entity.table.UserTable;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
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
    @Transactional
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
    @Select("select auth_name from t_auth where id = #{authId}")
    String selectAuthNameById(Integer authId);
}
