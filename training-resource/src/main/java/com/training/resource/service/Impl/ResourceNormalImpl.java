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
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

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
    private static final ConcurrentMap<String, String> PATH_CACHE = new ConcurrentHashMap<>();
    private static final ConcurrentMap<String, Boolean> UPLOAD_CACHE = new ConcurrentHashMap<>();
    private static final ConcurrentMap<String, String> VALID_SAME_HASH_FILE = new ConcurrentHashMap<>();

    /**
     * 上传资源文件的具体实现
     * @param req 请求实体
     * @return 根据处理结果返回消息
     * by organwalk 2023-10-21
     */
    @SneakyThrows
    @Override
    public MsgRespond uploadResourceNormalFile(ResourceNormalReq req) {

        Integer deptId = req.getDept_id();
        Integer tagId = req.getTag_id();
        Integer userId = req.getUp_id();
        String fileHash = req.getFile_hash();

        Boolean checkResult = UPLOAD_CACHE.get(fileHash);
        if (Objects.isNull(checkResult)){
            String validMsg = validUploadResource(deptId, tagId, userId);
            if (!validMsg.isBlank()){
                return MsgRespond.fail(validMsg);
            }
            // 缓存通过章节检查的结果
            UPLOAD_CACHE.put(fileHash, false);
        }

        // 检查是否具有相同哈希值的文件
        String filePath = VALID_SAME_HASH_FILE.get(fileHash);
        if (Objects.isNull(filePath)){
            filePath = fileUtil.checkEqualHashFilePath("normal", fileHash);
            if (Objects.isNull(filePath)){
                VALID_SAME_HASH_FILE.put(fileHash, "");
            }else {
                VALID_SAME_HASH_FILE.put(fileHash, filePath);
            }
        }
        
        if (VALID_SAME_HASH_FILE.get(fileHash).isBlank()){
            filePath = fileUtil.getNormalFilePath(userId, req.getFile_origin_name());

            // 获取文件保存路径
            String savePath = PATH_CACHE.get(fileHash);
            if (Objects.isNull(savePath)){
                PATH_CACHE.put(fileHash, filePath);
            }

            String processResult = fileUtil.chunkSaveFile(fileHash,
                    filePath,
                    PATH_CACHE.get(fileHash),
                    req.getFile_chunks_sum(),
                    req.getFile_now_chunk(),
                    req.getFile_size(),
                    req.getResource_file());

            if (Objects.isNull(processResult)){
                return MsgRespond.success("当前文件片段上传成功");
            }else if (Objects.equals(processResult, "true")){
                resourceNormalMapper.insertResourceNormal(new ResourceNormalTable(null,
                        req.getResource_name(), PATH_CACHE.get(fileHash), deptId, tagId, userId, fileUtil.getFileSaveDateTime(), req.getFile_hash()));

                PATH_CACHE.remove(fileHash);
                UPLOAD_CACHE.remove(fileHash);
                VALID_SAME_HASH_FILE.remove(fileHash);

                return MsgRespond.success("资源文件上传成功");
            }else {
                return MsgRespond.fail(processResult);
            }
        }

        UPLOAD_CACHE.remove(fileHash);
        VALID_SAME_HASH_FILE.remove(fileHash);

        resourceNormalMapper.insertResourceNormal(new ResourceNormalTable(null,
                req.getResource_name(), filePath, req.getDept_id(), req.getTag_id(), req.getUp_id(), fileUtil.getFileSaveDateTime(), req.getFile_hash()));
        return MsgRespond.success("资源文件上传成功");
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
    public ResponseEntity<?> downloadResourceNormalFile(String range, Integer rid) {
        String downloadUrl = resourceNormalMapper.selectPathByRid(rid);
        if (Objects.isNull(downloadUrl) || !new File(downloadUrl).exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        String fileName = URLEncoder.encode(resourceNormalMapper.selectFileNameByRid(rid), StandardCharsets.UTF_8);
        String fileExtension = downloadUrl.substring(downloadUrl.lastIndexOf("."));

        File file = new File(downloadUrl);
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + fileExtension + "\"");
        headers.add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);

        headers.setContentLength(file.length());

        if (range != null && range.startsWith("bytes=")) {
            String[] ranges = range.substring(6).split("-");
            long start = Long.parseLong(ranges[0]);
            long end = file.length() - 1;
            if (ranges.length > 1 && !ranges[1].isEmpty()) {
                end = Long.parseLong(ranges[1]);
            }

            headers.add(HttpHeaders.CONTENT_RANGE, "bytes " + start + "-" + end + "/" + file.length());

            HttpStatus status = HttpStatus.PARTIAL_CONTENT;
            headers.setContentLength(end - start + 1);

            return new ResponseEntity<>(resource, headers, status);
        }

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType(appConfig.tika().detect(file)))
                .body(resource);
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


    private static final ConcurrentMap<Integer, ResourceNormalDetailRespond> VALID_RESOURCE_EXIST = new ConcurrentHashMap<>();
    private static final ConcurrentMap<Integer, Boolean> VALID_EDIT_RESOURCE = new ConcurrentHashMap<>();
    private static final ConcurrentMap<Integer, String> VALID_EDIT_RESOURCE_SAME_HASH = new ConcurrentHashMap<>();
    private static final ConcurrentMap<Integer, String> EDIT_RESOURCE_PATH_CACHE = new ConcurrentHashMap<>();
    /**
     * 编辑指定资源文件信息具体实现
     * @param rid 部门ID
     * @param req 编辑请求实体
     * @return 根据处理结果返回相应提示消息
     * by organwalk 2023-10-22
     */
    @SneakyThrows
    @Override
    public MsgRespond editResourceNormalInfo(Integer rid, ResourceNormalReq req, String username, String auth) {

        String fileHash = req.getFile_hash();

        // 检查指定文件是否存在
        ResourceNormalDetailRespond detailObj = VALID_RESOURCE_EXIST.get(rid);
        if (Objects.isNull(detailObj)){
            detailObj = resourceNormalMapper.selectResourceNormalDetailByRidAnUpId(rid);
            if (Objects.isNull(detailObj)){
                return MsgRespond.fail("此资源文件不存在，请重新指定");
            }
            VALID_RESOURCE_EXIST.put(rid, detailObj);
        }

        // 校验
        Boolean validResult = VALID_EDIT_RESOURCE.get(rid);
        if (Objects.isNull(validResult)){
            String msg = validEditResource(req, auth, username);
            if (!msg.isBlank()){
                return MsgRespond.fail(msg);
            }
            VALID_EDIT_RESOURCE.put(rid, true);
        }

        // 检查是否具有相同哈希值的文件
        String filePath = VALID_EDIT_RESOURCE_SAME_HASH.get(rid);
        if (Objects.isNull(filePath)){
            filePath = fileUtil.checkEqualHashFilePath("normal", fileHash);
            if (Objects.isNull(filePath)){
                VALID_EDIT_RESOURCE_SAME_HASH.put(rid, "");
            }else {
                VALID_EDIT_RESOURCE_SAME_HASH.put(rid, filePath);
            }
        }

        if (VALID_EDIT_RESOURCE_SAME_HASH.get(rid).isBlank()){
            filePath = fileUtil.getNormalFilePath(req.getUp_id(), req.getFile_origin_name());
            // 获取文件保存路径
            String savePath = EDIT_RESOURCE_PATH_CACHE.get(rid);
            if (Objects.isNull(savePath)){
                EDIT_RESOURCE_PATH_CACHE.put(rid, filePath);
            }

            String processResult = fileUtil.chunkSaveFile(fileHash,
                    filePath,
                    EDIT_RESOURCE_PATH_CACHE.get(rid),
                    req.getFile_chunks_sum(),
                    req.getFile_now_chunk(),
                    req.getFile_size(),
                    req.getResource_file());

            if (Objects.isNull(processResult)){
                return MsgRespond.success("当前文件片段上传成功");
            }else if (Objects.equals(processResult, "true")){
                String oldFilePath = resourceNormalMapper.selectResourcePathByRid(rid);

                // 检查旧文件是否存在多重引用
                Integer pathMark = resourceNormalMapper.selectPathIsOverTwo(oldFilePath);
                if (pathMark != 2){
                    // 如果不存在，则说明同一份文件不存在多重引用，可删除服务器文件
                    File oldFile = new File(oldFilePath);
                    if (oldFile.exists() && !oldFile.delete()){
                        return MsgRespond.fail("文件系统错误，请稍后再试");
                    }
                }
                resourceNormalMapper.updateResourceNormalInfoByRid(req, req.getFile_hash(), EDIT_RESOURCE_PATH_CACHE.get(rid), fileUtil.getFileSaveDateTime(), rid);

                EDIT_RESOURCE_PATH_CACHE.remove(rid);
                VALID_RESOURCE_EXIST.remove(rid);
                VALID_EDIT_RESOURCE.remove(rid);
                VALID_EDIT_RESOURCE_SAME_HASH.remove(rid);
                return MsgRespond.success("资源文件上传成功");
            }else {
                return MsgRespond.fail(processResult);
            }
        }

        VALID_RESOURCE_EXIST.remove(rid);
        VALID_EDIT_RESOURCE.remove(rid);
        VALID_EDIT_RESOURCE_SAME_HASH.remove(rid);

        resourceNormalMapper.updateResourceNormalInfoByRid(req, req.getFile_hash(), filePath, fileUtil.getFileSaveDateTime(), rid);
        return MsgRespond.success("资源文件上传成功");
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
        System.out.println(filePath);
        if (!file.exists()){
            resourceNormalMapper.deleteResourceNormalByRid(rid);
            return MsgRespond.fail("此资源文件不存在");
        }
        // 检查是否有权限操作文件
        String checkAuth = dataUtil.checkResourceAuth(uid, auth, username);
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

    private String validUploadResource(Integer deptId, Integer tagId, Integer userId){
        // 对请求进行检查
        String checkInfo = checkResourceInfo(deptId, tagId);
        if (!checkInfo.isBlank()){
            return checkInfo;
        }
        // 检查指定用户是否存在
        Integer codeMark = (Integer) userClient.getUserAccountByUid(userId).get("code");
        if (Objects.equals(codeMark, 5005)){
            return "当前指定上传者不存在";
        }
        return "";
    }

    private String validEditResource(ResourceNormalReq req, String auth, String username){
        // 请求实体部门和分类标签存在性检查
        String checkInfo = checkResourceInfo(req.getDept_id(), req.getTag_id());
        if (!checkInfo.isBlank()){
            return checkInfo;
        }
        // 检查是否有权限操作文件
        String checkAuth = dataUtil.checkResourceAuth(req.getUp_id(), auth, username);
        if (!checkAuth.isBlank()){
            return checkAuth;
        }
        return "";
    }

}
