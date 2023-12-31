package com.training.resource.service.Impl;

import com.alibaba.fastjson.JSONObject;
import com.training.common.entity.DataFailRespond;
import com.training.common.entity.DataRespond;
import com.training.common.entity.DataSuccessRespond;
import com.training.common.entity.MsgRespond;
import com.training.common.entity.result.LessonInfo;
import com.training.resource.client.PlanClient;
import com.training.resource.client.TrainingClient;
import com.training.resource.entity.request.ResourceLessonReq;
import com.training.resource.entity.result.ResourceLessonInfo;
import com.training.resource.entity.table.ResourceLessonTable;
import com.training.resource.mapper.ResourceLessonMapper;
import com.training.resource.repository.ResourceLessonCache;
import com.training.resource.service.ResourceLessonService;
import com.training.resource.utils.DataUtil;
import com.training.resource.utils.FileResUtil;
import com.training.resource.utils.FileUtil;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 定义课程教材相关业务接口的具体实现
 * by organwalk 2023-11-02
 */
@Service
@Transactional
@AllArgsConstructor
public class ResourceLessonImpl implements ResourceLessonService {

    private final ResourceLessonMapper resourceLessonMapper;
    private final ResourceLessonCache resourceLessonCache;
    private final TrainingClient trainingClient;
    private final FileUtil fileUtil;
    private final DataUtil dataUtil;
    private final FileResUtil fileResUtil;
    private final PlanClient planClient;
    private static final ConcurrentMap<String, String> PATH_CACHE = new ConcurrentHashMap<>();
    private static final ConcurrentMap<Integer, Boolean> UPLOAD_CACHE = new ConcurrentHashMap<>();
    private static final ConcurrentMap<Integer, String> OLD_PATH_CACHE = new ConcurrentHashMap<>();
    private static final ConcurrentMap<Integer, Integer> VALID_SAME_PATH = new ConcurrentHashMap<>();
    private static final ConcurrentMap<Integer, String> VALID_SAME_HASH_FILE = new ConcurrentHashMap<>();
    /**
     * 上传教材资源文件具体实现
     * @param req 教材对象
     * @return 根据处理结果返回消息提示
     * by organwalk 2023-11-03
     */
    @SneakyThrows
    @Override
    public MsgRespond uploadResourceLesson(ResourceLessonReq req) {
        if (resourceLessonMapper.selectResourceLessonIdByChapterId(req.getChapter_id()) != null){
            return MsgRespond.fail("此课程章节已存在教材资源，无需重复上传");
        }

        Integer teacherId = req.getTeacher_id();
        Integer lessonId = req.getLesson_id();
        Integer chapterId = req.getChapter_id();

        Boolean checkResult = UPLOAD_CACHE.get(chapterId);
        if (Objects.isNull(checkResult)){
            // 检查教师存在性与教师、课程一致性
            String teacherMark = validTeacher(teacherId, lessonId);
            if (Objects.nonNull(teacherMark)){
                return MsgRespond.fail(teacherMark);
            }
            // 检查章节存在性
            String chapterMark = dataUtil.validChapter(lessonId, chapterId);
            if (Objects.nonNull(chapterMark)){
                return MsgRespond.fail(chapterMark);
            }
            // 缓存通过章节检查的结果
            UPLOAD_CACHE.put(chapterId, false);
        }

        // 检查是否具有相同哈希值的文件
        String filePath = fileUtil.checkEqualHashFilePath("lesson", req.getFile_hash());

        if (Objects.isNull(filePath)){
            filePath = fileUtil.getLessonFilePath(req.getTeacher_id(), req.getLesson_id(), chapterId, req.getFile_origin_name());
            String msg = uploadVideoLessonResource(filePath, req, "upload");

            if (Objects.equals(msg, "教材上传成功")) {
                UPLOAD_CACHE.remove(chapterId);
            }
            return Objects.equals(msg, "当前文件片段上传成功") || Objects.equals(msg, "教材上传成功")
                    ? MsgRespond.success(msg)
                    : MsgRespond.fail(msg);
        }

        UPLOAD_CACHE.remove(chapterId);
        resourceLessonMapper.insertLessonResource(new ResourceLessonTable(null,
                lessonId, teacherId, chapterId, filePath,  fileUtil.getFileSaveDateTime(), req.getFile_hash()));

        return MsgRespond.success("教材资源上传成功");
    }

    /**
     * 重传教材资源文件的具体实现
     * @param req 教材对象
     * @return 处理提示
     * by organwalk 2023-11-03
     */
    @Override
    public MsgRespond reUploadResourceLesson(ResourceLessonReq req) {

        Integer chapterId = req.getChapter_id();
        Boolean checkResult = UPLOAD_CACHE.get(chapterId);
        Integer idMark = null;

        if (Objects.isNull(checkResult)) {
            // 检查课程、章节、教程存在性
            idMark = resourceLessonMapper.selectIdByReUpdateArgs(req.getLesson_id(), req.getTeacher_id(), req.getChapter_id());
            if (Objects.isNull(idMark)){
                return MsgRespond.fail("重新检查指定的讲师、课程和章节是否正确");
            }

            // 删除该教材资源的视频测试题
            JSONObject res = planClient.deleteAllVideoTestByResourceLessonId(idMark);
            if (Objects.equals(res.getInteger("code"), 5005)
                    && !Objects.equals(res.getString("msg"), "该视频教程下不存在测试")){
                return MsgRespond.fail(res.getString("msg"));
            }

            UPLOAD_CACHE.put(chapterId, true);
        }


        String filePath = fileUtil.getLessonFilePath(req.getTeacher_id(), req.getLesson_id(), req.getChapter_id(), req.getFile_origin_name());
        // 获取原文件路径
        String oldFilePath = OLD_PATH_CACHE.get(chapterId);
        if (Objects.isNull(oldFilePath)){
            oldFilePath = resourceLessonMapper.selectOldFilePath(req.getChapter_id());
            OLD_PATH_CACHE.put(chapterId, oldFilePath);
        }
        // 检查是否具有两条相同路径的文件

        Integer pathMark = VALID_SAME_PATH.get(chapterId);
        if (Objects.isNull(pathMark)){
            pathMark = resourceLessonMapper.selectPathIsOverTwo(oldFilePath);
            VALID_SAME_PATH.put(chapterId, pathMark);
        }

        if (pathMark != 2){
            // 如果不存在，则说明同一份文件不存在多重引用，可删除服务器文件
            File oldFile = new File(oldFilePath);
            if (oldFile.exists() && !oldFile.delete()){
                return MsgRespond.fail("文件系统错误，请稍后再试");
            }
        }

        // 删除Redis缓存
        resourceLessonCache.deleteResourceLessonTypeAndPath(idMark);

        // 检查是否具有相同哈希值的文件
        String equalHashFilePath = VALID_SAME_HASH_FILE.get(chapterId);
        if (Objects.isNull(equalHashFilePath)){
            equalHashFilePath = fileUtil.checkEqualHashFilePath("lesson", req.getFile_hash());
            if (Objects.isNull(equalHashFilePath)){
                VALID_SAME_HASH_FILE.put(chapterId, "");
            }else {
                VALID_SAME_HASH_FILE.put(chapterId, equalHashFilePath);
            }
        }

        if (Objects.isNull(equalHashFilePath) || equalHashFilePath.isBlank()){
            String msg =  uploadVideoLessonResource(filePath, req, "reUpload");
            if (Objects.equals(msg, "教材上传成功")) {
                removePathCache(chapterId);
            }
            return Objects.equals(msg, "当前文件片段上传成功") || Objects.equals(msg, "教材上传成功")
                    ? MsgRespond.success(msg)
                    : MsgRespond.fail(msg);
        }

        removePathCache(chapterId);

        resourceLessonMapper.updateLessonResourcePath(equalHashFilePath, fileUtil.getFileSaveDateTime(), req.getLesson_id(), req.getTeacher_id(), req.getChapter_id());
        return MsgRespond.success("教材资源上传成功");

    }

    /**
     * 删除指定课程章节教材文件具体实现
     * @param chapterId 章节ID
     * @return 根据处理结果返回消息提示
     * by organwalk 2023-11-04
     */
    @Override
    public MsgRespond deleteOneLessonResource(Integer chapterId) {
        String msg = deleteResourceLesson(chapterId);
        if (!msg.isBlank()){
            return MsgRespond.fail(msg);
        }
        return MsgRespond.success("已成功删除此课程文件");
    }

    /**
     * 根据教材资源ID获取教材具体实现
     * @param rangeString 请求头的range
     * @param rlId 教材资源ID
     * @return 根据请求，将返回处理消息、文档教材或视频教材响应体
     */
    @SneakyThrows
    @Override
    public ResponseEntity<?> getResourceLessonById(String rangeString, Integer rlId, String random_str) {
        String filePath;
        String fileExtension;

        if (random_str.isBlank()){
            return fileResUtil.returnMsg("fail", "未提供随机字符串");
        }

        // 先从缓存中获取结果
        String cacheResult = resourceLessonCache.getResourceLessonTypeAndPath(rlId);
        if (!cacheResult.isBlank()){
            fileExtension = cacheResult.split("---")[0];
            filePath = cacheResult.split("---")[1];
        }else {
            // 如果缓存中不存在结果时，从数据库中获取
            filePath = resourceLessonMapper.selectLessonPathById(rlId);
            // 检查教材是否存在
            if (Objects.isNull(filePath)){
                return fileResUtil.returnMsg("fail", "未找到教材资源");
            }
            fileExtension = Objects.requireNonNull(filePath).substring(filePath.lastIndexOf("."));
            // 将查询结果保存于缓存中
            resourceLessonCache.saveResourceLessonTypeAndPath(rlId, fileExtension, filePath);
        }

        // 判断属于文档教材还是视频教材
        if (Objects.equals(fileExtension, ".md")){
            // 属于文档教材
            return fileResUtil.returnMarkdown(filePath);
        }
        // 属于视频教材
        return fileResUtil.returnVideo(rangeString, filePath);

    }

    /**
     * 根据课程ID获取该课程的教材泪飙
     * @param lessonId 课程ID
     * @return 返回教材列表，若为空，则返回提示消息
     */
    @Override
    public DataRespond getResourceLessonByLessonId(Integer lessonId) {
        List<ResourceLessonInfo> resourceLessonList = resourceLessonMapper.selectResourceLessonList(lessonId);
        if (resourceLessonList.isEmpty()){
            return new DataFailRespond("该课程尚未上传资源");
        }
        return new DataSuccessRespond("已成功获取该课程下的资源列表", resourceLessonList);
    }

    /**
     * 获取指定教材资源ID
     * @param lessonId 课程ID
     * @param chapterId 章节ID
     * @return 返回提示消息或ID
     * by organwalk 2023-11-18
     */
    @Override
    public DataRespond getResourceLessonId(Integer lessonId, Integer chapterId) {
        Integer id = resourceLessonMapper.getLessonResourceId(lessonId, chapterId);
        if (Objects.isNull(id)){
            return new DataFailRespond("该教材资源不存在");
        }
        return new DataSuccessRespond("已成功获取该教材资源ID", id);
    }

    /**
     * 获取指定教材资源类型
     * @param resourceId 教材资源ID
     * @return 返回提示消息或类型
     * by organwalk 2023-11-22
     */
    @Override
    public DataRespond getResourceLessonType(Integer resourceId) {
        String filePath = resourceLessonMapper.selectLessonPathById(resourceId);
        if (Objects.isNull(filePath)){
            return new DataFailRespond("不存在此教材资源，无法获取其类型");
        }

        String fileExtension = Objects.requireNonNull(filePath).substring(filePath.lastIndexOf("."));
        return new DataSuccessRespond("已成功获取教材资源后缀", fileExtension);
    }

    /**
     * 检查教师和课程是否存在和一致的内部方法
     * @param teacherId 教师ID
     * @param lessonId 课程ID
     * @return 消息提示，若为空，则表示校验通过
     * by organwalk by 2023-11-04
     */
    private String validTeacher(Integer teacherId, Integer lessonId){
        // 获取课程列表
        JSONObject lessonListObj = trainingClient.getLessonListByTeacher(teacherId, 999999, 0);
        // 检查讲师存在性
        if (Objects.equals(lessonListObj.getInteger("code"), 5005)){
            return lessonListObj.getString("msg");
        }
        // 检查讲师是否教授此课程
        List<LessonInfo> lessons = lessonListObj.getJSONArray("data").toJavaList(LessonInfo.class);
        boolean checkLessonId = lessons.stream().anyMatch(lessonInfo -> Objects.equals(lessonInfo.getId(), lessonId));
        if (!checkLessonId){
            return "该讲师未教授此课程";
        }
        return null;
    }

    /**
     * 定义上传视频教材资源的方法
     * @param filePath 文件路径
     * @param req 上传请求
     * @return 处理结果字符串
     */
    @SneakyThrows
    private String uploadVideoLessonResource(String filePath, ResourceLessonReq req, String type){

        // 获取文件保存路径
        String savePath = PATH_CACHE.get(req.getFile_hash());
        if (Objects.isNull(savePath)){
            PATH_CACHE.put(req.getFile_hash(), filePath);
        }

        String processResult = fileUtil.chunkSaveFile(req.getFile_hash(),
                filePath,
                PATH_CACHE.get(req.getFile_hash()),
                req.getFile_chunks_sum(),
                req.getFile_now_chunk(),
                req.getFile_size(),
                req.getResource_file());

        if (Objects.isNull(processResult)){
            return "当前文件片段上传成功";
        }else if (Objects.equals(processResult, "true")){
            if (Objects.equals(type, "upload")){
                resourceLessonMapper.insertLessonResource(new ResourceLessonTable(null,
                        req.getLesson_id(), req.getTeacher_id(), req.getChapter_id(), PATH_CACHE.get(req.getFile_hash()),  fileUtil.getFileSaveDateTime(), req.getFile_hash()));
            }else {
                resourceLessonMapper.updateLessonResourcePath(PATH_CACHE.get(req.getFile_hash()), fileUtil.getFileSaveDateTime(), req.getLesson_id(), req.getTeacher_id(), req.getChapter_id());
            }
            PATH_CACHE.remove(req.getFile_hash());
            return "教材上传成功";
        }else {
            return processResult;
        }
    }


    /**
     * 定义删除单个课程教材资源的方法
     * @param chapterId 章节ID
     * @return 成功删除时返回空字符串，否则返回相关提示信息
     * by organwalk 2023-11-23
     */
    private String deleteResourceLesson(Integer chapterId){
        // 检查章节存在性
        Integer idMark = resourceLessonMapper.selectResourceLessonIdByChapterId(chapterId);
        if (Objects.isNull(idMark)){
            return "重新检查指定的章节是否正确";
        }

        String oldFilePath = resourceLessonMapper.selectOldFilePath(chapterId);

        // 删除Redis缓存
        resourceLessonCache.deleteResourceLessonTypeAndPath(idMark);

        // 删除数据库记录
        resourceLessonMapper.deleteOneLessonResource(chapterId);

        // 检查是否具有相同哈希值的文件
        String fileHash = resourceLessonMapper.selectFileHashByInfo(chapterId);
        String filePath = fileUtil.checkEqualHashFilePath("lesson", fileHash);

        if (Objects.isNull(filePath)){
            // 如果不存在相同哈希值的文件，可以直接删除服务器存储记录
            File file = new File(oldFilePath);
            if (file.exists()){
                if (!file.delete()){
                    return "资源服务器处理错误，请稍后再试";
                }
            }
        }

        return "";
    }

    private void removePathCache(Integer chapterId){
        UPLOAD_CACHE.remove(chapterId);
        OLD_PATH_CACHE.remove(chapterId);
        VALID_SAME_PATH.remove(chapterId);
        VALID_SAME_HASH_FILE.remove(chapterId);
    }
}
