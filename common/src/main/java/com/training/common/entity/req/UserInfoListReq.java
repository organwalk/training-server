package com.training.common.entity.req;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 获取用户信息列表请求实体
 * by organwalk 2023-10-20
 */
@Data
@AllArgsConstructor
public class UserInfoListReq {
    private List<Integer> uid_list;
}
