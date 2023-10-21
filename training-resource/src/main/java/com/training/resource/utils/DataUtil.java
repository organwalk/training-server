package com.training.resource.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.training.common.entity.req.UserInfoListReq;
import com.training.resource.client.DeptClient;
import com.training.resource.client.UserClient;
import com.training.resource.entity.respond.ResourceNormalDetailRespond;
import com.training.resource.entity.respond.ResourceNormalRespond;
import com.training.resource.entity.result.DeptInfo;
import com.training.resource.entity.result.UpInfo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 定义数据处理的通用工具方法
 * by organwalk 2023-10-21
 */
@Component
@AllArgsConstructor
public class DataUtil {
    private final UserClient userClient;
    private final DeptClient deptClient;

    /**
     * 交换处理Uid列表为用户信息列表
     * @param rawDataList 原列表
     * @return 处理好的列表
     * by organwalk 2023-10-21
     */
    public List<ResourceNormalRespond> switchUidListToUserInfoList(List<ResourceNormalRespond> rawDataList) {

        // 提取up_id并存储到集合中
        List<Integer> upIds = new ArrayList<>();
        for (ResourceNormalRespond resource : rawDataList) {
            int upId = resource.getUp_id();
            upIds.add(upId);
        }
        // 调用用户服务的内部接口，批量查询用户信息
        JSONArray jsonUpInfoList = userClient.getUserInfoByUidList(new UserInfoListReq(upIds));
        List<UpInfo> upInfoList = JSON.parseArray(jsonUpInfoList.toJSONString(), UpInfo.class);

        for (int i = 0; i < rawDataList.size(); i++) {
            // 获取rawDataList中第i个元素
            ResourceNormalRespond rnr = rawDataList.get(i);
            // 获取upInfoList中第i个元素
            UpInfo ui = upInfoList.get(i);
            // 检查两个元素的id是否相等
            if (rnr.getUp_id().equals(ui.getId())) {
                // 如果相等，将rnr的upInfo属性设置为ui
                rnr.setUpInfo(ui);
            }
        }
        return rawDataList;
    }

    /**
     * 交换处理部门ID、上传者ID为对应的具体信息
     * @param obj 处理对象
     * @return 被处理好的obj
     * by organwalk 2023-10-21
     */
    public ResourceNormalDetailRespond switchDeptIdAndUpIdToInfo(ResourceNormalDetailRespond obj){
        // 调用部门服务的内部接口，查询部门信息
        JSONObject jsonObject = deptClient.getDeptInfo(obj.getDept_id()).getJSONObject("data");
        DeptInfo deptInfo = JSON.parseObject(jsonObject.toJSONString(), DeptInfo.class);
        System.out.println(deptInfo);
        obj.setDeptInfo(deptInfo);
        // 调用用户服务的内部接口，查询用户信息
        List<Integer> upIds = new ArrayList<>();
        upIds.add(obj.getUp_id());
        JSONArray jsonUpInfoList = userClient.getUserInfoByUidList(new UserInfoListReq(upIds));
        List<UpInfo> upInfoList = JSON.parseArray(jsonUpInfoList.toJSONString(), UpInfo.class);
        obj.setUpInfo(upInfoList.get(0));
        return obj;
    }
}
