package com.training.user.service.Impl;

import com.alibaba.fastjson.JSONObject;
import com.training.common.entity.*;
import com.training.user.client.DeptClient;
import com.training.user.client.ResourceClient;
import com.training.user.entity.request.AccountReq;
import com.training.user.entity.request.AllAccountInfoReq;
import com.training.user.entity.request.LoginReq;
import com.training.user.entity.request.UserAccountInfoReq;
import com.training.user.entity.respond.AuthInfoRespond;
import com.training.user.entity.respond.AuthRespond;
import com.training.user.entity.result.UserInfo;
import com.training.user.entity.table.AuthTable;
import com.training.user.entity.table.UserTable;
import com.training.user.mapper.UserMapper;
import com.training.user.reposity.UserCache;
import com.training.user.service.UserService;
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
    private final DeptClient deptClient;
    private final ResourceClient resourceClient;

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
    public MsgRespond createAccount(AccountReq req) {
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
    public MsgRespond editUserAccountInfoByUid(Integer uid, AllAccountInfoReq req) {
        if (!req.getPassword().isBlank() && req.getPassword().length() < 6){
            return MsgRespond.fail("password长度不可小于6位");
        }
        // 获取编辑前的旧密码和权限
        UserTable oldAccountInfo = userMapper.selectUserAccountByUid(uid);
        if (Objects.isNull(oldAccountInfo)){
            return MsgRespond.fail("修改失败，该用户不存在");
        }
        String oldPassword = oldAccountInfo.getPassword();
        Integer oldAuthId = oldAccountInfo.getAuthId();
        String newPassword = req.getPassword();
        if (Objects.nonNull(newPassword) && !newPassword.isBlank()){
            // 哈希加密密码，并进行修改操作
            req.setPassword(encoder.encode(newPassword));
        }else {
            req.setPassword(null);
        }
        // 检查用户是否加入了部门
        JSONObject deptRes = deptClient.getDeptIdByUid(uid);
        if (Objects.equals(deptRes.getInteger("code"), 2002)){
            // 检查是否更改了权限
            if (!Objects.equals(req.getAuth_id(), oldAuthId)){
                return MsgRespond.fail("该用户以员工身份加入了部门，无法修改权限。请在解除其部门关系后重试");
            }
        }
        userMapper.updateUserAccountInfoByUid(uid, req);
        // 检查密码或权限是否更改，判断是否需要销毁令牌
        if ((!encoder.matches(newPassword, oldPassword) && Objects.nonNull(newPassword)) || !Objects.equals(req.getAuth_id(), oldAuthId)){
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
        // 检查用户是否上传了资源
        Integer codeMark = resourceClient.getTagListByDeptId(uid, 1,0).getInteger("code");
        if (Objects.equals(codeMark, 2002)){
            return MsgRespond.fail("该用户存在上传资源，无法删除。请先清除此用户上传的资源");
        }
        // 检查用户是否加入了部门
        JSONObject deptRes = deptClient.getDeptIdByUid(uid);
        if (Objects.equals(deptRes.getInteger("code"), 2002)){
            // 检查是否成功删除用户在部门内的关系
            JSONObject memberInfo = deptClient.deleteMember(deptRes.getInteger("data"), uid);
            if (Objects.equals(memberInfo.get("code"), 5005)){
                return MsgRespond.fail((String) memberInfo.get("msg"));
            }
        }
        userMapper.deleteUserAccountByUid(uid);
        userCache.deleteAccessToken(oldInfo.getUsername());
        return MsgRespond.success("已成功删除此用户");
    }

    /**
     * 用户自行修改账号信息具体实现
     * @param uid 用户ID
     * @param username 用户名
     * @return 处理结果消息
     * by organwalk 2023-10-19
     */
    @Override
    public MsgRespond editUserAccountInfoByUser(Integer uid, String username, UserAccountInfoReq req) {
        // 判断用户是否存在
        UserTable oldInfo = userMapper.selectUserAccountByUid(uid);
        if (Objects.isNull(oldInfo)) {
            return MsgRespond.fail("修改失败，该用户不存在");
        }
        // 判断是否是用户自行修改
        if (!Objects.equals(oldInfo.getId(), uid)){
            return MsgRespond.fail("修改失败，该操作非用户本人修改");
        }


        String newPassword = req.getPassword();
        String oldPassword = oldInfo.getPassword();
        if (Objects.nonNull(req.getPassword())){
            req.setPassword(encoder.encode(newPassword));
        }
        userMapper.updateUserAccountInfoByUser(uid, req);
        // 检查密码是否更改，判断是否需要销毁令牌
        if (Objects.nonNull(req.getPassword())){
            if (!encoder.matches(newPassword, oldPassword)){
                userCache.deleteAccessToken(oldInfo.getUsername());
            }
        }
        return MsgRespond.success("已成功更新此账号信息");
    }

    /**
     * 获取所有教师/员工的信息列表具体实现
     * @param type 类别，1.teacher:获取教师信息列表；2.worker:获取员工信息列表
     * @param pageSize 读取记录数
     * @param offset 从第几条记录继续读取
     * @return 返回信息列表，若为空则返回提示信息
     */
    @Override
    public DataRespond getUserInfoListByType(String type, Integer pageSize, Integer offset) {
        // 转换type为权限ID
        Integer authId = switch (type){
            case "teacher" -> 2;
            case "worker" -> 1;
            default -> 0;
        };
        // 检查对应权限下人员列表是否为空
        Integer sumMark = userMapper.selectUserAccountSumByAuthId(authId);
        if (sumMark == 0){
            return new DataFailRespond("当前类型下人员列表为空");
        }
        return new DataSuccessRespond(
                "已成功获取用户信息列表",
                userMapper.selectUserInfoByType(authId, pageSize, offset)
        );
    }

    /**
     * 获取指定用户信息具体实现
     * @param uid 用户ID
     * @return 返回指定用户信息，若为空则返回提示信息
     * by organwalk 2023-10-19
     */
    @Override
    public DataRespond getUserInfoByUid(Integer uid) {
        UserInfo userInfo = userMapper.selectUserInfoByUid(uid);
        return Objects.nonNull(userInfo)
                ? new DataSuccessRespond("已成功获取指定用户信息", userInfo)
                : new DataFailRespond("该用户不存在");
    }

    /**
     * 根据用户ID列表获取用户信息列表具体实现
     * @param uidList 用户ID列表
     * @return 返回指定用户信息列表
     * by organwalk 2023-10-19
     */
    @Override
    public DataRespond getUserInfoListByUidList(List<Integer> uidList) {
        List<UserInfo> userInfoList = userMapper.batchSelectUserByUidList(uidList);
        if (userInfoList.isEmpty()){
            return new DataFailRespond("无法获取到用户信息");
        }
        return new DataSuccessRespond("已成功获取用户信息", userInfoList);
    }

    /**
     * 根据信息进行模糊搜索具体实习
     * @param keyword 信息
     * @param type 类别
     * @param pageSize 读取记录数
     * @param offset 从第几条开始继续读取
     * @return 列表或错误提示
     * by organwalk 2023-10-26
     */
    @Override
    public DataRespond getSearchByKeyword(String keyword, Integer type, Integer pageSize, Integer offset) {
        Integer sumMark = userMapper.selectUserAccountSumByKeywordAuthId(keyword, type);
        if (sumMark == 0){
            return new DataFailRespond("未搜索到相关记录");
        }
        return new DataPagingSuccessRespond("已成功搜索到相关记录", sumMark,
                userMapper.searchByKeyword(keyword, type, pageSize, offset));
    }
}
