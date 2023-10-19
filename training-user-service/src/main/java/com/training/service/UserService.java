package com.training.service;

import com.training.entity.request.CreateAccountReq;
import com.training.entity.request.EditAccountByUserReq;
import com.training.entity.request.EditAccountReq;
import com.training.entity.request.LoginReq;
import com.training.entity.respond.AuthInfoRespond;
import entity.DataRespond;
import entity.MsgRespond;

public interface UserService {
    // 获取权限列表
    DataRespond getAuthList();

    // 创建用户账号
    MsgRespond createAccount(CreateAccountReq req);

    // 用户登录获取认证
    DataRespond getUserAuth(LoginReq req);

    // 获取指定用户授权信息
    AuthInfoRespond getUserAuthInfo(String username);

    // 获取所有用户账号列表
    DataRespond getUserAccountByType(Integer type, Integer page_size, Integer offset);

    // 获取指定用户的账号信息
    DataRespond getUserAccountByUid(Integer uid);

    // 编辑指定用户的账号信息
    MsgRespond editUserAccountInfoByUid(Integer uid, EditAccountReq req);

    // 删除指定用户
    MsgRespond deleteAccountByUid(Integer uid);

    // 用户自行修改账号信息
    MsgRespond editUserAccountInfoByUser(Integer uid, String username, EditAccountByUserReq req);

    // 获取所有教师/员工的信息列表
    DataRespond getUserInfoListByType(String type, Integer pageSize, Integer offset);

    // 获取指定用户信息
    DataRespond getUserInfoByUid(Integer uid);
}
