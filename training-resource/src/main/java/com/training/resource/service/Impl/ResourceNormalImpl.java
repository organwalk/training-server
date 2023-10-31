package com.training.resource.service.Impl;

import com.alibaba.fastjson.JSONObject;
import com.training.common.entity.*;
import com.training.resource.client.DeptClient;
import com.training.resource.client.UserClient;
import com.training.resource.config.AppConfig;
import com.training.resource.entity.request.ResourceNormalReq;
import com.training.resource.entity.respond.ResourceNormalAllListRespond;
import com.training.resource.entity.respond.ResourceNormalDetailRespond;
import com.training.resource.entity.respond.ResourceNormalRespond;
import com.training.resource.entity.table.ResourceNormalTable;
import com.training.resource.mapper.ResourceNormalMapper;
import com.training.resource.mapper.TagMapper;
import com.training.resource.service.ResourceNormalService;
import com.training.resource.utils.DataUtil;
import com.training.resource.utils.FileUtil;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
@Transactional
public class ResourceNormalImpl implements ResourceNormalService {
    private final DeptClient deptClient;
    private final UserClient userClient;
    private final ResourceNormalMapper resourceNormalMapper;
    private final TagMapper tagMapper;
    private final FileUtil fileUtil;
    private final DataUtil dataUtil;
    private final AppConfig appConfig;

    /**
     * 上传资源文件的具体实现
     * @param req 请求实体
     * @return 根据处理结果返回消息
     * by organwalk 2023-10-21
     */
    @Override
    public MsgRespond uploadResourceNormalFile(ResourceNormalReq req) {
        // 对请求进行检查
        String checkInfo = checkResourceInfo(req.getDept_id(), req.getTag_id());
        if (!checkInfo.isBlank()){
            return MsgRespond.fail(checkInfo);
        }
        // 检查指定用户是否存在
        Integer codeMark = (Integer) userClient.getUserAccountByUid(req.getUp_id()).get("code");
        if (Objects.equals(codeMark, 5005)){
            return MsgRespond.fail("当前指定上传者不存在");
        }
        // 获取文件保存路径
        String filePath = fileUtil.getNormalFilePath(req.getUp_id(), req.getResource_file());
        // 保存文件
        try {
            req.getResource_file().transferTo(new File(filePath));
        } catch (IOException e) {
            return MsgRespond.fail("内部服务错误，文件上传失败，请稍后再试");
        }
        resourceNormalMapper.insertResourceNormal(new ResourceNormalTable(null,
                req.getResource_name(), filePath, req.getDept_id(), req.getTag_id(), req.getUp_id(), fileUtil.getFileSaveDateTime()));
        return MsgRespond.success("资源上传成功");
    }

    /**
     * 获取上传资源列表具体实现
     * @param deptId 部门ID
     * @param tagId 分类标签ID
     * @param pageSize 读取记录数
     * @param offset 从第几条读起
     * @return 数据列表，若为空，则返回提示语句
     */
    @Override
    public DataRespond getResourceNormalList(Integer deptId, Integer tagId, Integer pageSize, Integer offset) {
        // 对请求进行检查
        String checkInfo = checkResourceInfo(deptId, tagId);
        if (!checkInfo.isBlank()){
            return new DataFailRespond(checkInfo);
        }
        // 获取记录总数
        Integer sumMark = resourceNormalMapper.selectResourceListSumByDeptIdAndTagId(deptId, tagId);
        if (sumMark == 0){
            return new DataFailRespond("该部门分类标签下资源列表为空");
        }
        List<ResourceNormalRespond> resourceList = resourceNormalMapper.selectResourceListByDeptIdAndTagId(deptId, tagId, pageSize, offset);
        return new DataPagingSuccessRespond("已成功获取此资源列表", sumMark, dataUtil.switchUidListToUserInfoList(resourceList));
    }

    /**
     * 下载指定资源文件
     * @param rid 资源ID
     * @return 下载资源，或者失败提示
     * by organwalk 2023-10-21
     */
    @SneakyThrows
    @Override
    public ResponseEntity<?> downloadResourceNormalFile(Integer rid) {
        String downloadUrl = resourceNormalMapper.selectPathByRid(rid);
        if (Objects.isNull(downloadUrl) || !new File(downloadUrl).exists()){
            return ResponseEntity.status(HttpStatus.OK).body(MsgRespond.fail("未找到资源，请修改后重试"));
        }
        String fileName = URLEncoder.encode(resourceNormalMapper.selectFileNameByRid(rid), StandardCharsets.UTF_8);
        String fileExtension = downloadUrl.substring(downloadUrl.lastIndexOf("."));
        Resource file = new FileSystemResource(downloadUrl);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(appConfig.tika().detect(new File(downloadUrl))))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + fileExtension + "\"")
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION) // 暴露Content-Disposition字段
                .body(file);
    }

    /**
     * 获取指定资源文件详情具体实现
     * @param rid 资源ID
     * @return 资源文件详情或错误提示
     * by organwalk 2023-10-22
     */
    @Override
    public DataRespond getResourceNormalDetail(Integer rid) {
        // 检查指定文件是否存在
        ResourceNormalDetailRespond detailObj = resourceNormalMapper.selectResourceNormalDetailByRidAnUpId(rid);
        if (Objects.isNull(detailObj)){
            return new DataFailRespond("此资源文件不存在，请重新指定");
        }
        // 获取部门和人员详细并进行转化
        return new DataSuccessRespond("已成功获取此资源文件详情", dataUtil.switchDeptIdAndUpIdToInfo(detailObj));
    }

    /**
     * 编辑指定资源文件信息具体实现
     * @param rid 部门ID
     * @param req 编辑请求实体
     * @return 根据处理结果返回相应提示消息
     * by organwalk 2023-10-22
     */
    @Override
    public MsgRespond editResourceNormalInfo(Integer rid, ResourceNormalReq req, String username, String auth) {
        // 检查指定文件是否存在
        ResourceNormalDetailRespond detailObj = resourceNormalMapper.selectResourceNormalDetailByRidAnUpId(rid);
        if (Objects.isNull(detailObj)){
            return MsgRespond.fail("此资源文件不存在，请重新指定");
        }
        // 请求实体部门和分类标签存在性检查
        String checkInfo = checkResourceInfo(req.getDept_id(), req.getTag_id());
        if (!checkInfo.isBlank()){
            return MsgRespond.fail(checkInfo);
        }
        // 检查是否有权限操作文件
        String checkAuth = checkResourceAuth(req.getUp_id(), auth, username);
        if (!checkAuth.isBlank()){
            return MsgRespond.fail(checkAuth);
        }
        // 检查文件是否存在
        String filePath = null;
        if (Objects.nonNull(req.getResource_file()) && !req.getResource_file().isEmpty()){
            // 获取原文件路径
            String oldFilePath = resourceNormalMapper.selectResourcePathByRid(rid);
            // 获取文件保存路径
            filePath = fileUtil.getNormalFilePath(req.getUp_id(), req.getResource_file());
            try {
                // 保存文件
                req.getResource_file().transferTo(new File(filePath));
                // 删除原文件
                File oldFile = new File(oldFilePath);
                if (oldFile.delete()){
                    resourceNormalMapper.updateResourceNormalInfoByRid(req, filePath, fileUtil.getFileSaveDateTime(), rid);
                    return MsgRespond.success("已成功编辑此文件");
                }
            } catch (IOException e) {
                return MsgRespond.fail("内部服务错误，文件上传失败，请稍后再试");
            }
        }
        resourceNormalMapper.updateResourceNormalInfoByRid(req, filePath, fileUtil.getFileSaveDateTime(), rid);
        return MsgRespond.success("已成功编辑此文件");
    }

    /**
     * 删除指定资源文件的具体实现
     * @param rid 资源ID
     * @return 根据处理结果返回提示消息
     * by organwalk 2023-10-22
     */
    @Override
    public MsgRespond deleteResourceNormal(Integer rid, Integer uid, String username, String auth) {
        // 检查文件在数据库中的记录是否存在
        String filePath = resourceNormalMapper.selectResourcePathByRid(rid);
        if (Objects.isNull(filePath)){
            return MsgRespond.fail("此资源文件不存在");
        }
        // 检查文件在服务器中的记录是否存在
        File file = new File(filePath);
        if (!file.exists()){
            resourceNormalMapper.deleteResourceNormalByRid(rid);
            return MsgRespond.fail("此资源文件不存在");
        }
        // 检查是否有权限操作文件
        String checkAuth = checkResourceAuth(uid, auth, username);
        if (!checkAuth.isBlank()){
            return MsgRespond.fail(checkAuth);
        }
        // 删除文件
        if (file.delete()){
            resourceNormalMapper.deleteResourceNormalByRid(rid);
        }
        return MsgRespond.success("已成功删除此资源文件");
    }

    /**
     * 获取指定用户上传的资源文件列表具体实现
     * @param upId 上传用户ID
     * @param pageSize 读取记录数
     * @param offset 从第几条读起
     * @return 资源文件列表，若为空，则返回错误提示
     */
    @Override
    public DataRespond getResourceNormalListByUpId(Integer upId, Integer pageSize, Integer offset) {
        // 检查指定上传者是否存在
        JSONObject userInfo = userClient.getUserAccountByUid(upId);
        Integer codeMark = userInfo.getInteger("code");
        if (Objects.equals(codeMark, 5005)) {
            return new DataFailRespond("当前指定上传者不存在");
        }
        // 获取列表总数
        Integer sumMark = resourceNormalMapper.selectResourceListSumByUpId(upId);
        if (sumMark == 0) {
            return new DataFailRespond("当前用户的上传资源文件列表为空");
        }
        List<ResourceNormalRespond> resourceList = resourceNormalMapper.selectResourceListByUpId(upId, pageSize, offset);

        return new DataPagingSuccessRespond("已成功获取该用户的上传资源列表", sumMark,
                dataUtil.switchUidListToUserInfoList(resourceList));
    }

    /**
     * 获取上传资源所有列表具体实现
     * @param pageSize 读取记录数
     * @param offset 从第几条读起
     * @return List或者失败消息
     *
     * by organwalk 2023-10-29
     */
    @Override
    public DataRespond getAllResourceNormalList(Integer pageSize, Integer offset) {
        // 获取列表总数
        Integer sumMark = resourceNormalMapper.selectResourceListSum();
        if (sumMark == 0){
            return new DataFailRespond("上传资源文件列表为空");
        }
        List<ResourceNormalAllListRespond> result = resourceNormalMapper.selectResourceList(pageSize, offset);
        return new DataPagingSuccessRespond("已成功获取上传资源文件列表", sumMark,
                dataUtil.switchUidAndDeptListToUserAndDeptInfoList(result));
    }

    /**
     * 获取模糊查询普通资源的结果
     * @param deptId 部门ID（非必须）
     * @param tagId 分类标签ID （非必须）
     * @param keyword 关键词搜索
     * @param pageSize 读取记录数
     * @param offset 第几条读起
     * @return List 或 失败消息
     *
     * by organwalk 2023-10-29
     */
    @Override
    public DataRespond getNormalResourceByKeyword(Integer deptId, Integer tagId, String keyword, Integer pageSize, Integer offset) {
        Integer sumMark = resourceNormalMapper.selectResourceListByKeywordSum(deptId, tagId, keyword, pageSize, offset);
        if (sumMark == 0){
            return new DataFailRespond("该关键词查询结果为空");
        }
        List<ResourceNormalAllListRespond> result = resourceNormalMapper.selectResourceListByKeyword(deptId, tagId, keyword, pageSize, offset);
        return new DataPagingSuccessRespond("已成功查询到相关结果", sumMark, dataUtil.switchUidAndDeptListToUserAndDeptInfoList(result));

    }

    /**
     * 普通资源文件请求的通用检查
     * @param deptId 部门ID
     * @param tagId 分类标签ID
     * @return 空字符串或提示消息
     * by organwalk 2023-10-21
     */
    private String checkResourceInfo(Integer deptId, Integer tagId){
        // 检查指定部门是否存在
        Integer deptMark = deptClient.getDeptExistStatus(deptId);
        if (Objects.isNull(deptMark)){
            return "当前指定的部门不存在，请修改后重试";
        }
        // 检查指定标签是否存在
        Integer tagMark = tagMapper.selectTagExistById(tagId);
        if (Objects.isNull(tagMark)){
            return "当前指定的分类标签不存在，请联系管理员创建此标签";
        }
        return "";
    }

    private String checkResourceAuth(Integer upId, String auth, String username){
        // 检查指定上传者是否存在
        JSONObject userInfo = userClient.getUserAccountByUid(upId);
        Integer codeMark = userInfo.getInteger("code");
        if (Objects.equals(codeMark, 5005)){
            return "当前指定上传者不存在";
        }
        // 检查身份是否是管理员
        if (!Objects.equals(auth, "admin")){
            // 检查是否是上传者本人
            String realUsername = userInfo.getJSONObject("data").getString("username");
            if (!Objects.equals(username, realUsername)){
                return "当前身份非资源上传者本人，无法进行编辑";
            }
        }
        return "";
    }
}
