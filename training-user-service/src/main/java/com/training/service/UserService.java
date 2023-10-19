package com.training.service;

import com.training.entity.request.CreateAccountReq;
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
}
