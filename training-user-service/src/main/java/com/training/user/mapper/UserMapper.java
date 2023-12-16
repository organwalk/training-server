package com.training.user.mapper;

import com.training.user.entity.request.AllAccountInfoReq;
import com.training.user.entity.request.UserAccountInfoReq;
import com.training.user.entity.result.UserInfo;
import com.training.user.entity.table.AuthTable;
import com.training.user.entity.table.UserTable;
import org.apache.ibatis.annotations.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 定义t_user表的Mapper接口
 * by organwalk 2023-10-18
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
            "ORDER BY id DESC",
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
                    one = @One(select = "com.training.user.mapper.UserMapper.selectAuthNameById"))

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
    @Update({"<script>",
            "update t_user set",
            "real_name = #{req.real_name}, ",
            "<if test='req.password != null'>",
            "password = #{req.password},",
            "</if>",
            "mobile = #{req.mobile}, ",
            "auth_id = #{req.auth_id} ",
            "where id = #{uid}",
            "</script>"
    })
    void updateUserAccountInfoByUid(@Param("uid") Integer uid, @Param("req") AllAccountInfoReq req);

    // 根据用户ID删除用户
    @Delete("delete from t_user where id = #{uid}")
    void deleteUserAccountByUid(Integer uid);

    // 根据用户ID用户自行编辑账号信息
    @Update("<script>" +
            "update t_user" +
            "<set>" +
            "<if test='req.password != null'> password = #{req.password},</if>" +
            "<if test='req.mobile != null'> mobile = #{req.mobile},</if>" +
            "</set>" +
            "where id = #{uid}" +
            "</script>")
    void updateUserAccountInfoByUser(@Param("uid") Integer uid, @Param("req") UserAccountInfoReq req);

    // 根据类别获取教师/员工信息列表
    @Select({
            "select id, real_name, mobile, auth_id, extra from t_user " +
            "where auth_id = #{authId} " +
            "limit #{pageSize} offset #{offset}",
    })
    @Results(id = "userInfo", value = {
            @Result(id = true, column = "id", property = "id"),
            @Result(column = "real_name", property = "realName"),
            @Result(column = "mobile", property = "mobile"),
            @Result(column = "extra", property = "extra"),
            @Result(column = "auth_id", property = "auth", javaType = AuthTable.class,
                    one = @One(select = "com.training.user.mapper.UserMapper.selectAuthNameById"))

    })
    List<UserInfo> selectUserInfoByType(@Param("authId") Integer authId,
                                        @Param("pageSize") Integer pageSize,
                                        @Param("offset") Integer offset
    );

    // 根据ID获取用户信息
    @Select("select id, real_name, mobile, auth_id, extra from t_user where id = #{uid}")
    @ResultMap("userInfo")
    UserInfo selectUserInfoByUid(Integer uid);

    // 根据用户ID列表获取用户信息列表
    @Select("<script>" +
            "<foreach item='id' collection='uidList' separator='UNION ALL'>" +
            "SELECT id, real_name, mobile, auth_id, extra FROM t_user WHERE id = #{id}" +
            "</foreach>" +
            "</script>")
    @ResultMap("userInfo")
    List<UserInfo> batchSelectUserByUidList(@Param("uidList") List<Integer> uidList);

    //  根据信息对用户账号信息进行模糊搜索列表总数
    @Select("SELECT count(*) FROM t_user " +
            "WHERE username LIKE CONCAT('%', #{keyword}, '%') " +
            "   OR password LIKE CONCAT('%', #{keyword}, '%') " +
            "   OR real_name LIKE CONCAT('%', #{keyword}, '%') " +
            "   OR mobile LIKE CONCAT('%', #{keyword}, '%') and auth_id = #{authId}")
    Integer selectUserAccountSumByKeywordAuthId(@Param("keyword") String keyword,
                                         @Param("authId") Integer authId);
    // 根据信息对用户账号信息进行模糊搜索
    @Select("SELECT * FROM t_user " +
            "WHERE (username LIKE CONCAT('%', #{keyword}, '%') " +
            "   OR password LIKE CONCAT('%', #{keyword}, '%') " +
            "   OR real_name LIKE CONCAT('%', #{keyword}, '%') " +
            "   OR mobile LIKE CONCAT('%', #{keyword}, '%') )and auth_id = #{authId} ORDER BY id DESC limit #{pageSize} offset #{offset}")
    @ResultMap("UserTable")
    List<UserTable> searchByKeyword(@Param("keyword") String keyword,
                                    @Param("authId") Integer authId,
                                    @Param("pageSize") Integer pageSize,
                                    @Param("offset") Integer offset);
}
