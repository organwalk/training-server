package com.training.service.Impl;

import com.training.entity.request.CreateAccountReq;
import com.training.entity.request.EditAccountReq;
import com.training.entity.request.LoginReq;
import com.training.entity.respond.AuthInfoRespond;
import com.training.entity.respond.AuthRespond;
import com.training.entity.table.AuthTable;
import com.training.entity.table.UserTable;
import com.training.mapper.UserMapper;
import com.training.reposity.UserCache;
import com.training.service.UserService;
import entity.*;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * 用户服务具体实现
 * by organwalk 2023-10-18
 */
@Service
@AllArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder encoder;
    private final UserCache userCache;

    /**
     * 获取权限列表具体实现
     * @return 返回权限列表，若为空则返回错误提示
     * by organwalk 2023-10-18
     */
    @Override
    public DataRespond getAuthList() {
        List<AuthTable> authList = userMapper.selectAuthList();
        System.out.println(authList);
        return authList.isEmpty()
                ? new DataFailRespond("权限列表为空，请稍后再试")
                : new DataSuccessRespond("已成功获取权限列表", authList);
    }

    /**
     * 创建用户账号具体实现
     * @param req 请求实体
     * @return 根据处理状态返回成功或失败信息
     * by organwalk 2023-10-18
     */
    @Override
    public MsgRespond createAccount(CreateAccountReq req) {
        // 检查username是否重复
        String username = req.getUsername();
        Integer checkMark = userMapper.selectExistUsername(username);
        if (checkMark == 1) {
            return MsgRespond.fail("用户名" + username + "已存在，请修改后重试");
        }
        // 对隐私信息进行哈希加密
        String password = encoder.encode(req.getPassword());
        // 创建账号
        UserTable table = new UserTable(null, username, password, req.getReal_name(), req.getMobile(), req.getAuth_id(), null);
        Integer insertMark = userMapper.insertUser(table);
        // 检查账号是否创建成功
        return insertMark > 0
                ? MsgRespond.success("已成功创建此账号")
                : MsgRespond.fail("创建账号失败，请重试");
    }

    /**
     * 用户登录获取认证具体实现
     *
     * @param req 请求实体
     * @return 若用户名或密码正确，则进行授权，否则返回错误提示
     * by organwalk 2023-10-18
     */
    @Override
    public DataRespond getUserAuth(LoginReq req) {
        // 检查username是否存在
        String username = req.getUsername();
        Integer checkMark = userMapper.selectExistUsername(username);
        if (checkMark != 1) {
            return new DataFailRespond("用户名或密码错误");
        }
        // 将用户明文密码与数据库加密密码进行比对,检查密码是否正确
        String realPassword = userMapper.selectPasswordByUsername(username);
        if (!encoder.matches(req.getPassword(), realPassword)) {
            return new DataFailRespond("用户名或密码错误");
        }
        // 构建授权信息
        String accessToken = encoder.encode(UUID.randomUUID().toString());
        userCache.saveAccessToken(username, accessToken);
        UserTable authInfo = userMapper.selectAuthInfoByUsername(username); // 获取uid和auth_id
        AuthTable auth = userMapper.selectAuthNameById(authInfo.getAuthId());  // 利用auth_id获取auth_name
        AuthRespond authRespond = new AuthRespond(authInfo.getId(), username, accessToken, auth.getAuthName());
        return new DataSuccessRespond("用户认证通过", authRespond);
    }

    /**
     * 获取指定用户授权信息具体实现
     * @param username 用户名
     * @return 授权信息响应对象
     * by organwalk 2023-10-19
     */
    @Override
    public AuthInfoRespond getUserAuthInfo(String username) {
        // 获取通行令牌
        String accessToken = userCache.getAccessToken(username);
        // 获取权限
        UserTable userTable = userMapper.selectAuthInfoByUsername(username);
        AuthTable auth = userMapper.selectAuthNameById(userTable.getAuthId());
        return new AuthInfoRespond(auth.getAuthName(), accessToken);
    }

    /**
     * 获取指定类别的用户账号列表具体实现
     * @param type 类别 1：none，2：teacher，3：admin，4.：全部
     * @param page_size 需要查询多少条记录
     * @param offset 从第几条记录开始查询
     * @return 账号列表，若为空则返回提示消息
     * by organwalk 2023-10-19
     */
    @Override
    public DataRespond getUserAccountByType(Integer type, Integer page_size, Integer offset) {
        Integer sumMark = userMapper.selectUserAccountSumByAuthId(type);
        if (sumMark == 0){
            return new DataFailRespond("当前条件下获取结果为空");
        }
        List<UserTable> accountList = userMapper.selectUserAccountByAuthId(type, page_size, offset);
        return new DataPagingSuccessRespond("已成功获取所有用户账号信息", sumMark, accountList);
    }

    /**
     * 获取指定用户的账号信息具体实现
     * @param uid 用户ID
     * @return 账号信息，若为空则返回提示消息
     * by organwalk 2023-10-19
     */
    @Override
    public DataRespond getUserAccountByUid(Integer uid) {
        UserTable accountInfo = userMapper.selectUserAccountByUid(uid);
        return Objects.nonNull(accountInfo)
                ? new DataSuccessRespond("已成功获取该用户的账号信息", accountInfo)
                : new DataFailRespond("该用户账号信息不存在");
    }

    /**
     * 编辑指定用户的账号信息具体实现
     * @param uid 用户ID
     * @return 处理结果消息
     * by organwalk 2023-10-19
     */
    @Override
    public MsgRespond editUserAccountInfoByUid(Integer uid, EditAccountReq req) {
        // 获取编辑前的旧密码和权限
        UserTable oldAccountInfo = userMapper.selectUserAccountByUid(uid);
        if (Objects.isNull(oldAccountInfo)){
            return MsgRespond.fail("修改失败，该用户不存在");
        }
        String oldPassword = oldAccountInfo.getPassword();
        Integer oldAuthId = oldAccountInfo.getAuthId();
        String newPassword = req.getPassword();
        // 哈希加密密码，并进行修改操作
        req.setPassword(encoder.encode(req.getPassword()));
        userMapper.updateUserAccountInfoByUid(uid, req);
        // 检查密码或权限是否更改，判断是否需要销毁令牌
        if (!encoder.matches(newPassword, oldPassword) || !Objects.equals(req.getAuth_id(), oldAuthId)){
            userCache.deleteAccessToken(oldAccountInfo.getUsername());
        }
        return MsgRespond.success("已成功更新此账号信息");
    }

    /**
     * 删除指定用户的具体实现
     * @param uid 用户ID
     * @return 处理结果消息
     * by organwalk 2023-10-19
     */
    @Override
    public MsgRespond deleteAccountByUid(Integer uid) {
        UserTable oldInfo = userMapper.selectUserAccountByUid(uid);
        if (Objects.isNull(oldInfo)){
            return MsgRespond.fail("删除失败，该用户不存在");
        }
        userMapper.deleteUserAccountByUid(uid);
        userCache.deleteAccessToken(oldInfo.getUsername());
        return MsgRespond.success("已成功删除此用户");
    }
}
