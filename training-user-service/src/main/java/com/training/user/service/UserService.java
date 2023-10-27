package com.training.user.service;

import com.training.common.entity.DataRespond;
import com.training.common.entity.MsgRespond;
import com.training.user.entity.request.AccountReq;
import com.training.user.entity.request.AllAccountInfoReq;
import com.training.user.entity.request.LoginReq;
import com.training.user.entity.request.UserAccountInfoReq;
import com.training.user.entity.respond.AuthInfoRespond;
import com.training.user.entity.result.UserInfo;

import java.util.List;

public interface UserService {
    // 获取权限列表
    DataRespond getAuthList();

    // 创建用户账号
    MsgRespond createAccount(AccountReq req);

    // 用户登录获取认证
    DataRespond getUserAuth(LoginReq req);

    // 获取指定用户授权信息
    AuthInfoRespond getUserAuthInfo(String username);

    // 获取所有用户账号列表
    DataRespond getUserAccountByType(Integer type, Integer page_size, Integer offset);

    // 获取指定用户的账号信息
    DataRespond getUserAccountByUid(Integer uid);

    // 编辑指定用户的账号信息
    MsgRespond editUserAccountInfoByUid(Integer uid, AllAccountInfoReq req);

    // 删除指定用户
    MsgRespond deleteAccountByUid(Integer uid);

    // 用户自行修改账号信息
    MsgRespond editUserAccountInfoByUser(Integer uid, String username, UserAccountInfoReq req);

    // 获取所有教师/员工的信息列表
    DataRespond getUserInfoListByType(String type, Integer pageSize, Integer offset);

    // 获取指定用户信息
    DataRespond getUserInfoByUid(Integer uid);

    // 根据用户ID列表获取用户信息列表
    List<UserInfo> getUserInfoListByUidList(List<Integer> uidList);

    // 根据信息模糊搜索获取用户列表
    DataRespond getSearchByKeyword(String keyword, Integer type, Integer pageSize, Integer offset);
}
