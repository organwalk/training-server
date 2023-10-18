package com.training.service.Impl;

import com.training.entity.request.CreateAccountReq;
import com.training.entity.request.LoginReq;
import com.training.entity.respond.AuthRespond;
import com.training.entity.table.AuthTable;
import com.training.entity.table.UserTable;
import com.training.mapper.UserMapper;
import com.training.reposity.UserCache;
import com.training.service.UserService;
import entity.DataFailRespond;
import entity.DataRespond;
import entity.DataSuccessRespond;
import entity.MsgRespond;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder encoder;
    private final UserCache userCache;

    /**
     * 获取权限列表具体实现
     *
     * @return 返回权限列表，若为空则返回错误提示
     * <p>
     * by organwalk 2023-10-18
     */
    @Override
    public DataRespond getAuthList() {
        List<AuthTable> authList = userMapper.selectAuthList();
        return authList.isEmpty()
                ? DataSuccessRespond.success("已成功获取权限列表", authList)
                : DataFailRespond.fail("权限列表为空，请稍后再试");
    }

    /**
     * 创建用户账号具体实现
     *
     * @param req 请求实体
     * @return 根据处理状态返回成功或失败信息
     * <p>
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
     * <p>
     * by organwalk 2023-10-18
     */
    @Override
    public DataRespond getUserAuth(LoginReq req) {
        // 检查username是否存在
        String username = req.getUsername();
        Integer checkMark = userMapper.selectExistUsername(username);
        if (checkMark != 1) {
            return DataFailRespond.fail("用户名或密码错误");
        }
        // 将用户明文密码与数据库加密密码进行比对,检查密码是否正确
        String realPassword = userMapper.selectPasswordByUsername(username);
        if (!encoder.matches(req.getPassword(), realPassword)) {
            return DataFailRespond.fail("用户名或密码错误");
        }
        // 构建授权信息
        String accessToken = encoder.encode(UUID.randomUUID().toString());
        userCache.saveAccessToken(username, accessToken);
        UserTable authInfo = userMapper.selectAuthInfoByUsername(username); // 获取uid和auth_id
        String authName = userMapper.selectAuthNameById(authInfo.getAuthId());  // 利用auth_id获取auth_name
        AuthRespond authRespond = new AuthRespond(authInfo.getId(), username, accessToken, authName);
        return DataSuccessRespond.success("用户认证通过", authRespond);
    }
}
