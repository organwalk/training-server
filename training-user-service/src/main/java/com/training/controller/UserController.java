package com.training.controller;

import com.training.entity.request.CreateAccountReq;
import com.training.entity.request.LoginReq;
import com.training.entity.respond.AuthInfoRespond;
import com.training.service.UserService;
import entity.DataRespond;
import entity.MsgRespond;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    // 获取权限列表
    @GetMapping("/v3/user/auth")
    public DataRespond getAuthList() {
        return userService.getAuthList();
    }

    // 创建用户账号
    @PostMapping("/v3/user/account")
    public MsgRespond createAccount(@Validated @RequestBody CreateAccountReq req) {
        return userService.createAccount(req);
    }

    // 用户登录获取认证
    @PostMapping("/v1/user/auth")
    public DataRespond getUserAuth(@Validated @RequestBody LoginReq req) {
        return userService.getUserAuth(req);
    }

    // (仅内部) 获取指定用户授权信息
    @GetMapping("/v1/user/auth/{username}")
    public AuthInfoRespond getAccessToken(@PathVariable String username){
        return userService.getUserAuthInfo(username);
    }

}
