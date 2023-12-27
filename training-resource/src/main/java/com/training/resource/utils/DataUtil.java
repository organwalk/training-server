package com.training.resource.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.training.common.entity.req.DeptListReq;
import com.training.common.entity.req.UserInfoListReq;
import com.training.common.entity.result.ChapterInfo;
import com.training.resource.client.DeptClient;
import com.training.resource.client.TrainingClient;
import com.training.resource.client.UserClient;
import com.training.resource.entity.respond.ResourceNormalAllListRespond;
import com.training.resource.entity.respond.ResourceNormalDetailRespond;
import com.training.resource.entity.respond.ResourceNormalRespond;
import com.training.resource.entity.result.DeptInfo;
import com.training.resource.entity.result.UpInfo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 定义数据处理的通用工具方法
 * by organwalk 2023-10-21
 */
@Component
@AllArgsConstructor
public class DataUtil {
    private final UserClient userClient;
    private final DeptClient deptClient;
    private final TrainingClient trainingClient;

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
        List<UpInfo> upInfoList = getUpInfoList(upIds);
        if (upInfoList.isEmpty()){
            return new ArrayList<>();
        }

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
        if (Objects.equals(jsonObject.getInteger("code"), 5005)){
            return null;
        }
        DeptInfo deptInfo = JSON.parseObject(jsonObject.toJSONString(), DeptInfo.class);
        obj.setDeptInfo(deptInfo);
        // 调用用户服务的内部接口，查询用户信息
        List<Integer> upIds = new ArrayList<>();
        upIds.add(obj.getUp_id());
        JSONObject jsonUpInfoList = userClient.getUserInfoByUidList(new UserInfoListReq(upIds));
        if (Objects.equals(jsonUpInfoList.getInteger("code"), 5005)){
            return null;
        }
        List<UpInfo> upInfoList = JSON.parseArray(jsonUpInfoList.toJSONString(), UpInfo.class);
        obj.setUpInfo(upInfoList.get(0));
        return obj;
    }

    /**
     * 交换处理Uid列表为用户信息列表DeptId列表为部门信息列表
     * @param rawDataList 原列表
     * @return 处理好的列表
     * by organwalk 2023-10-21
     */
    public List<ResourceNormalAllListRespond> switchUidAndDeptListToUserAndDeptInfoList(List<ResourceNormalAllListRespond> rawDataList) {

        // 提取up_id和dept_id并存储到集合中
        List<Integer> upIds = new ArrayList<>();
        List<Integer> deptIds = new ArrayList<>();
        for (ResourceNormalAllListRespond resource : rawDataList) {
            upIds.add(resource.getUp_id());
            deptIds.add(resource.getDept_id());
        }

        List<UpInfo> upInfoList = getUpInfoList(upIds);
        if (upInfoList.isEmpty()){
            return new ArrayList<>();
        }
        List<DeptInfo> deptInfoList = getDeptInfoList(deptIds);
        if (deptInfoList.isEmpty()){
            return new ArrayList<>();
        }

        for (int i = 0; i < rawDataList.size(); i++) {
            // 获取rawDataList中第i个元素
            ResourceNormalAllListRespond allListRespond = rawDataList.get(i);
            // 获取List中第i个元素
            UpInfo ui = upInfoList.get(i);
            DeptInfo di = deptInfoList.get(i);
            // 检查两个元素的id是否相等
            if (allListRespond.getUp_id().equals(ui.getId())) {
                // 如果相等，将allListRespond的Info属性替换
                allListRespond.setUpInfo(ui);
            }
            if (allListRespond.getDept_id().equals(di.getId())) {
                allListRespond.setDeptInfo(di);
            }
        }
        return rawDataList;
    }

    /**
     * 检查章节是否存在
     * @param lessonId 课程ID
     * @param chapterId 章节ID
     * @return 消息提示， 若为空，则表示校验通过
     * by organwalk by 2023-11-04
     */
    public String validChapter(Integer lessonId, Integer chapterId){
        JSONObject chapterListObj = trainingClient.getChapterListByLesson(lessonId);
        if (Objects.equals(chapterListObj.getInteger("code"), 5005)){
            return chapterListObj.getString("msg");
        }
        List<ChapterInfo> chapters = chapterListObj.getJSONArray("data").toJavaList(ChapterInfo.class);
        boolean checkChapterId = chapters.stream().anyMatch(chapterInfo -> Objects.equals(chapterInfo.getId(), chapterId));
        if (!checkChapterId){
            return "该课程下不存在此章节";
        }
        return null;
    }

    public String checkResourceAuth(Integer upId, String auth, String username){
        // 检查指定上传者是否存在
        JSONObject userInfo = userClient.getUserAccountByUid(upId);
        Integer codeMark = userInfo.getInteger("code");
        if (Objects.equals(codeMark, 5005)){
            return userInfo.getString("msg");
        }
        // 检查身份是否是管理员
        if (!Objects.equals(auth, "admin")){
            // 检查是否是上传者本人
            String realUsername = userInfo.getJSONObject("data").getString("username");
            if (!Objects.equals(username, realUsername)){
                return "当前身份非资源上传者本人，无法进行操作";
            }
        }
        return "";
    }

    private List<UpInfo> getUpInfoList(List<Integer> upIds){
        // 调用用户服务的内部接口，批量查询用户信息
        JSONObject jsonUpInfoList = userClient.getUserInfoByUidList(new UserInfoListReq(upIds));
        if (Objects.equals(jsonUpInfoList.getInteger("code"), 5005)){
            return new ArrayList<>();
        }
        return JSON.parseArray(jsonUpInfoList.toJSONString(), UpInfo.class);
    }

    private List<DeptInfo> getDeptInfoList(List<Integer> deptIds){
        // 调用部门服务的内部接口，批量查询部门信息
        JSONObject jsonDeptInfoList = deptClient.getDeptInfoByDeptIdList(new DeptListReq(deptIds));
        if (Objects.equals(jsonDeptInfoList.getInteger("code"), 5005)){
            return new ArrayList<>();
        }
        return JSON.parseArray(jsonDeptInfoList.toJSONString(), DeptInfo.class);
    }
}
