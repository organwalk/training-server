package com.training.user.exceptions;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.training.common.entity.DataFailRespond;
import com.training.common.entity.DataRespond;
import com.training.common.entity.MsgRespond;
import com.training.common.entity.req.UserInfoListReq;
import com.training.user.entity.request.UserAccountInfoReq;
import com.training.user.entity.respond.AuthInfoRespond;

public class GlobeBlockException {
    public static DataRespond blockedGetAccountInfoByUid(Integer uid, BlockException e){
        return new DataFailRespond("用户服务异常，无法正常获取用户账号信息");
    }
    public static DataRespond blockedGetUserInfoByUidList(UserInfoListReq req, BlockException e){
        return new DataFailRespond("用户服务异常，无法正常获取用户信息列表");
    }
    public static AuthInfoRespond blockedGetAccessToken(String username, BlockException e){
        return new AuthInfoRespond("none", "user-service error");
    }
    public static MsgRespond blockedEditUserAccountInfoByUser(Integer uid, UserAccountInfoReq req, String username, BlockException e){
        return MsgRespond.fail("用户服务异常，无法正常编辑账号信息");
    }
}
