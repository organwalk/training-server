package com.training.resource.service.Impl;

import com.alibaba.fastjson.JSONObject;
import com.training.common.entity.DataFailRespond;
import com.training.common.entity.DataRespond;
import com.training.common.entity.DataSuccessRespond;
import com.training.common.entity.MsgRespond;
import com.training.common.entity.result.LessonInfo;
import com.training.resource.client.TrainingClient;
import com.training.resource.entity.request.ResourceLessonReq;
import com.training.resource.entity.result.ResourceLessonInfo;
import com.training.resource.entity.table.ResourceLessonTable;
import com.training.resource.mapper.ResourceLessonMapper;
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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
@Transactional
@AllArgsConstructor
public class ResourceLessonImpl implements ResourceLessonService {

    private final ResourceLessonMapper resourceLessonMapper;
    private final TrainingClient trainingClient;
    private final FileUtil fileUtil;
    private final DataUtil dataUtil;
    private final FileResUtil fileResUtil;
    private static final ConcurrentMap<String, String> Path_CACHE = new ConcurrentHashMap<>();
    /**
     * 上传教材资源文件具体实现
     * @param req 教材对象
     * @return 根据处理结果返回消息提示
     * by organwalk 2023-11-03
     */
    @SneakyThrows
    @Override
    public MsgRespond uploadResourceLesson(ResourceLessonReq req) {
        if (resourceLessonMapper.selectLessonIdByChapterId(req.getChapter_id()) != null){
            return MsgRespond.fail("此课程章节已存在教材资源，无需重复上传");
        }

        Integer teacherId = req.getTeacher_id();
        Integer lessonId = req.getLesson_id();

        // 检查教师存在性与教师、课程一致性
        String teacherMark = validTeacher(teacherId, lessonId);
        if (Objects.nonNull(teacherMark)){
            return MsgRespond.fail(teacherMark);
        }
        // 检查章节存在性
        String chapterMark = dataUtil.validChapter(lessonId, req.getChapter_id());
        if (Objects.nonNull(chapterMark)){
            return MsgRespond.fail(chapterMark);
        }


        // 检查是否具有相同哈希值的文件
        String filePath = fileUtil.checkEqualHashFilePath("lesson", req.getFile_hash());
        if (Objects.isNull(filePath)){
            filePath = fileUtil.getLessonFilePath(req.getTeacher_id(), req.getLesson_id(), req.getChapter_id(), req.getFile_origin_name());
            String msg =  uploadVideoLessonResource(filePath, req, "upload");
            return Objects.equals(msg, "当前文件片段上传成功") || Objects.equals(msg, "教材上传成功")
                    ? MsgRespond.success(msg)
                    : MsgRespond.fail(msg);
        }

        resourceLessonMapper.insertLessonResource(new ResourceLessonTable(null,
                lessonId, teacherId, req.getChapter_id(), filePath,  fileUtil.getFileSaveDateTime(), req.getFile_hash()));

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
        // 检查课程、章节、教程存在性
        Integer idMark = resourceLessonMapper.selectIdByReUpdateArgs(req.getLesson_id(), req.getTeacher_id(), req.getChapter_id());
        if (Objects.isNull(idMark)){
            return MsgRespond.fail("重新检查指定的讲师、课程和章节是否正确");
        }

        String filePath = fileUtil.getLessonFilePath(req.getTeacher_id(), req.getLesson_id(), req.getChapter_id(), req.getFile_origin_name());
        // 获取原文件路径
        String oldFilePath = resourceLessonMapper.selectOldFilePath(req.getLesson_id(), req.getTeacher_id(), req.getChapter_id());
        // 检查是否具有两条相同路径的文件
        Integer pathMark = resourceLessonMapper.selectPathIsOverTwo(oldFilePath);
        if (pathMark != 2){
            // 如果不存在，则说明同一份文件不存在多重引用，可删除服务器文件
            File oldFile = new File(oldFilePath);
            if (oldFile.exists()){
                if (!oldFile.delete()){
                    return MsgRespond.fail("文件系统错误，请稍后再试");
                }
            }
        }
        String msg =  uploadVideoLessonResource(filePath, req, "reUpload");
        resourceLessonMapper.updateLessonResourcePath(filePath, fileUtil.getFileSaveDateTime(), req.getLesson_id(), req.getTeacher_id(), req.getChapter_id());
        return Objects.equals(msg, "当前文件片段上传成功") || Objects.equals(msg, "教材上传成功")
                ? MsgRespond.success(msg)
                : MsgRespond.fail(msg);
    }

    /**
     * 删除指定课程章节教材文件具体实现
     * @param teacherId 教师ID
     * @param lessonId 课程ID
     * @param chapterId 章节ID
     * @return 根据处理结果返回消息提示
     * by organwalk 2023-11-04
     */
    @Override
    public MsgRespond deleteOneLessonResource(Integer teacherId, Integer lessonId, Integer chapterId) {

        // 检查课程、章节、教程存在性
        Integer idMark = resourceLessonMapper.selectIdByReUpdateArgs(lessonId, teacherId, chapterId);
        if (Objects.isNull(idMark)){
            return MsgRespond.fail("重新检查指定的讲师、课程和章节是否正确");
        }

        // 删除数据库记录
        resourceLessonMapper.deleteOneLessonResource(teacherId, lessonId, chapterId);

        // 检查是否具有相同哈希值的文件
        String fileHash = resourceLessonMapper.selectFileHashByInfo(teacherId, lessonId, chapterId);
        String filePath = fileUtil.checkEqualHashFilePath("lesson", fileHash);
        if (Objects.isNull(filePath)){
            // 如果不存在相同哈希值的文件，可以直接删除服务器存储记录
            String oldFilePath = resourceLessonMapper.selectOldFilePath(lessonId, teacherId, chapterId);
            File file = new File(oldFilePath);
            if (file.exists()){
                if (!file.delete()){
                    return MsgRespond.fail("资源服务器处理错误，请稍后再试");
                }
            }
        }

        return MsgRespond.success("已成功删除此课程文件");
    }

    /**
     * 删除指定课程下所有教材文件
     * @param teacherId 教师ID
     * @param lessonId 课程ID
     * @return 根据处理结果返回消息提示
     * by organwalk 2023-11-04
     */
    @Override
    public MsgRespond deleteAllLessonResource(Integer teacherId, Integer lessonId) {
        // 检查教师、课程存在性
        Integer idMark = resourceLessonMapper.selectIdByDeleteAllLessonArgs(lessonId, teacherId);
        if (Objects.isNull(idMark)) {
            return MsgRespond.fail("重新检查指定的讲师、课程是否正确");
        }

        // 获取需要删除的路径和不能删除的路径列表
        String folderPath = fileUtil.getLessonFolderPath(teacherId, lessonId);
        List<String> nonDeletePaths = resourceLessonMapper.findDuplicateFilePathsByTeacherId(teacherId, lessonId);

        // 删除数据库记录
        resourceLessonMapper.deleteAllLessonResource(teacherId, lessonId);

        // 删除文件
        File file = new File(folderPath);
        if (file.exists()) {
            try {
                Files.walk(Path.of(folderPath))
                        .sorted(Comparator.reverseOrder())
                        .filter(path -> nonDeletePaths.isEmpty() || !nonDeletePaths.contains(path.toString()))
                        .map(Path::toFile)
                        .forEach(File::delete);
            } catch (IOException e) {
                return MsgRespond.fail("内部服务错误，删除失败，请稍后重试");
            }
        }

        return MsgRespond.success("已成功删除此课程教材文件");
    }

    /**
     * 根据教材资源ID获取教材具体实现
     * @param rangeString 请求头的range
     * @param rlId 教材资源ID
     * @return 根据请求，将返回处理消息、文档教材或视频教材响应体
     */
    @SneakyThrows
    @Override
    public ResponseEntity<?> getResourceLessonById(String rangeString, Integer rlId) {

        // 检查教材是否存在
        String filePath = resourceLessonMapper.selectLessonPathById(rlId);
        if (Objects.isNull(filePath)){
            return fileResUtil.returnMsg("fail", "未找到教材资源");
        }

        // 判断属于文档教材还是视频教材
        String fileExtension = Objects.requireNonNull(filePath).substring(filePath.lastIndexOf("."));
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
        String savePath = Path_CACHE.get(req.getFile_hash());
        if (Objects.isNull(savePath)){
            Path_CACHE.put(req.getFile_hash(), filePath);
        }
        String processResult = fileUtil.chunkSaveFile(req.getFile_hash(), filePath, req.getFile_chunks_sum(), req.getFile_now_chunk(), req.getFile_size(), req.getResource_file());
        if (Objects.isNull(processResult)){
            return "当前文件片段上传成功";
        }else if (Objects.equals(processResult, "true")){
            if (Objects.equals(type, "upload")){
                resourceLessonMapper.insertLessonResource(new ResourceLessonTable(null,
                        req.getLesson_id(), req.getTeacher_id(), req.getChapter_id(), Path_CACHE.get(req.getFile_hash()),  fileUtil.getFileSaveDateTime(), req.getFile_hash()));
            }else {
                resourceLessonMapper.updateLessonResourcePath(Path_CACHE.get(req.getFile_hash()), fileUtil.getFileSaveDateTime(), req.getLesson_id(), req.getTeacher_id(), req.getChapter_id());
            }
            Path_CACHE.remove(req.getFile_hash());
            return "教材上传成功";
        }else {
            return processResult;
        }
    }
}
