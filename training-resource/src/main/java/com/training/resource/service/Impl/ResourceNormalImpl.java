package com.training.resource.service.Impl;

import com.training.common.entity.*;
import com.training.resource.client.DeptClient;
import com.training.resource.client.UserClient;
import com.training.resource.config.AppConfig;
import com.training.resource.entity.request.ResourceNormalReq;
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
     * 获取上传资源列表具体是西安
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
        if (Objects.isNull(downloadUrl)){
            return ResponseEntity.status(HttpStatus.OK).body(MsgRespond.fail("未找到资源，请修改后重试"));
        }
        Resource file = new FileSystemResource(downloadUrl);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(appConfig.tika().detect(new File(downloadUrl))))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }

    /**
     * 获取指定资源文件详情具体实现
     * @param rid 资源ID
     * @return 资源文件详情或错误提示
     */
    @Override
    public DataRespond getResourceNormalDetail(Integer rid) {
        ResourceNormalDetailRespond detailObj = resourceNormalMapper.selectResourceNormalDetailByRidAnUpId(rid);
        if (Objects.isNull(detailObj)){
            return new DataFailRespond("此资源文件不存在，请重新指定");
        }
        // 获取部门和人员详细并进行转化
        return new DataSuccessRespond("已成功获取此资源文件详情", dataUtil.switchDeptIdAndUpIdToInfo(detailObj));
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
}
