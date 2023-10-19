package com.training.controller;

import com.training.entity.request.CreateAccountReq;
import com.training.entity.request.EditAccountReq;
import com.training.entity.request.LoginReq;
import com.training.entity.respond.AuthInfoRespond;
import com.training.service.UserService;
import entity.DataRespond;
import entity.MsgRespond;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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

    // 获取指定类别用户账号列表
    @GetMapping("/v3/user/account/{type}/{page_size}/{offset}")
    public DataRespond getAccountListByType(@PathVariable
                                                @Min(value = 1, message = "auth字段必须是1、2、3或4的整数类型")
                                                @Max(value = 4, message = "auth字段必须是1、2、3或4的整数类型")
                                            Integer type,
                                            @PathVariable
                                                @Min(value = 1, message = "page_size必须为大于1的整数")
                                                @Digits(integer = Integer.MAX_VALUE, fraction = 0)
                                            Integer page_size,
                                            @PathVariable
                                                @Min(value = 0, message = "offset必须为大于或等于0的整数")
                                                @Digits(integer = Integer.MAX_VALUE, fraction = 0)
                                            Integer offset){
        return userService.getUserAccountByType(type, page_size, offset);
    }

    // 获取指定用户的账号信息
    @GetMapping("/v1/user/account/{uid}")
    public DataRespond getAccountInfoByUid(@PathVariable
                                               @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "uid必须为纯数字字段")
                                           Integer uid){
        return userService.getUserAccountByUid(uid);
    }

    // 编辑指定用户的账号信息
    @PutMapping("/v3/user/account/{uid}")
    public MsgRespond editUserAccountByUid(@PathVariable
                                               @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "uid必须为纯数字字段")
                                           Integer uid,
                                           @Validated @RequestBody EditAccountReq req){
        return userService.editUserAccountInfoByUid(uid, req);
    }

    // 删除指定用户
    @DeleteMapping("/v3/user/account/{uid}")
    public MsgRespond deleteAccountByUid(@PathVariable
                                             @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "uid必须为纯数字字段")
                                         Integer uid){
        return userService.deleteAccountByUid(uid);
    }

}
