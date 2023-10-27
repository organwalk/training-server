package com.training.user.controller;

import com.training.common.entity.DataRespond;
import com.training.common.entity.MsgRespond;
import com.training.common.entity.req.UserInfoListReq;
import com.training.user.entity.request.AccountReq;
import com.training.user.entity.request.AllAccountInfoReq;
import com.training.user.entity.request.LoginReq;
import com.training.user.entity.request.UserAccountInfoReq;
import com.training.user.entity.respond.AuthInfoRespond;
import com.training.user.entity.result.UserInfo;
import com.training.user.service.UserService;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    // 获取权限列表
    @GetMapping("/v3/auth")
    public DataRespond getAuthList() {
        return userService.getAuthList();
    }

    // 创建用户账号
    @PostMapping("/v3/account")
    public MsgRespond createAccount(@Validated @RequestBody AccountReq req) {
        return userService.createAccount(req);
    }

    // 用户登录获取认证
    @PostMapping("/v1/auth")
    public DataRespond getUserAuth(@Validated @RequestBody LoginReq req) {
        return userService.getUserAuth(req);
    }

    // (仅内部) 获取指定用户授权信息
    @GetMapping("/v1/auth/{username}")
    public AuthInfoRespond getAccessToken(@PathVariable String username) {
        return userService.getUserAuthInfo(username);
    }

    // 获取指定类别用户账号列表
    @GetMapping("/v3/account/{type}/{page_size}/{offset}")
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
                                            Integer offset) {
        return userService.getUserAccountByType(type, page_size, offset);
    }

    // 获取指定用户的账号信息
    @GetMapping("/v1/account/{uid}")
    public DataRespond getAccountInfoByUid(@PathVariable
                                           @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "uid必须为纯数字字段")
                                           Integer uid) {
        return userService.getUserAccountByUid(uid);
    }

    // 编辑指定用户的账号信息
    @PutMapping("/v3/account/{uid}")
    public MsgRespond editUserAccountByUid(@PathVariable
                                           @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "uid必须为纯数字字段")
                                           Integer uid,
                                           @Validated @RequestBody AllAccountInfoReq req) {
        return userService.editUserAccountInfoByUid(uid, req);
    }

    // 删除指定用户
    @DeleteMapping("/v3/account/{uid}")
    public MsgRespond deleteAccountByUid(@PathVariable
                                         @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "uid必须为纯数字字段")
                                         Integer uid) {
        return userService.deleteAccountByUid(uid);
    }

    // 用户自行修改账号信息
    @PutMapping("/v1/account/{uid}")
    public MsgRespond editUserAccountInfoByUser(@PathVariable
                                                @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "uid必须为纯数字字段")
                                                Integer uid,
                                                @Validated @RequestBody UserAccountInfoReq req,
                                                @RequestHeader(name = "username") String username) {
        return userService.editUserAccountInfoByUser(uid, username, req);
    }

    // 获取所有教师/员工的信息列表
    @GetMapping("/v3/info/{type}/{page_size}/{offset}")
    public DataRespond getUserInfoListByType(@PathVariable
                                             @Pattern(regexp = "^(teacher|worker)$", message = "type字段只能是teacher或worker")
                                             String type,
                                             @PathVariable
                                             @Min(value = 1, message = "page_size必须为大于1的整数")
                                             @Digits(integer = Integer.MAX_VALUE, fraction = 0)
                                             Integer page_size,
                                             @PathVariable
                                             @Min(value = 0, message = "offset必须为大于或等于0的整数")
                                             @Digits(integer = Integer.MAX_VALUE, fraction = 0)
                                             Integer offset) {
        return userService.getUserInfoListByType(type, page_size, offset);
    }

    // 获取指定用户信息
    @GetMapping("/v1/info/{uid}")
    public DataRespond getUserInfoByUid(@PathVariable
                                        @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "uid必须为纯数字字段")
                                        Integer uid) {
        return userService.getUserInfoByUid(uid);
    }

    // 根据用户ID列表获取用户信息列表
    @GetMapping("/v1/info/list")
    public List<UserInfo> getUserInfoByUidList(@RequestBody UserInfoListReq req) {
        return userService.getUserInfoListByUidList(req.getUid_list());
    }

    // 根据用户模糊信息获取账号信息
    @GetMapping("/v3/account/keyword/{keyword}/{type}/{page_size}/{offset}")
    public DataRespond getUserAccountByKeyword(@PathVariable
                                               @NotBlank(message = "keyword不能为空")
                                               String keyword,
                                               @PathVariable
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
                                               Integer offset) {
        return userService.getSearchByKeyword(keyword, type, page_size, offset);
    }
}
