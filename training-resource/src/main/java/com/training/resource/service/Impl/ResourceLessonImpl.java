package com.training.resource.service.Impl;

import com.alibaba.fastjson.JSONObject;
import com.training.common.entity.MsgRespond;
import com.training.common.entity.result.ChapterInfo;
import com.training.common.entity.result.LessonInfo;
import com.training.resource.client.TrainingClient;
import com.training.resource.entity.request.ResourceLessonReq;
import com.training.resource.entity.table.ResourceLessonTable;
import com.training.resource.mapper.ResourceLessonMapper;
import com.training.resource.service.ResourceLessonService;
import com.training.resource.utils.DataUtil;
import com.training.resource.utils.FileUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
@AllArgsConstructor
public class ResourceLessonImpl implements ResourceLessonService {

    private final ResourceLessonMapper resourceLessonMapper;
    private final TrainingClient trainingClient;
    private final FileUtil fileUtil;
    private final DataUtil dataUtil;
    /**
     * 上传教材资源文件具体实现
     * @param req 教材对象
     * @return 根据处理结果返回消息提示
     * by organwalk 2023-11-03
     */
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
        // 保存教材文件
        String filePath = fileUtil.getLessonFilePath(teacherId, lessonId, req.getChapter_id(), req.getResource_file());
        // 保存文件
        try {
            File file = new File(filePath);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs(); // 创建路径中所有不存在的目录
            }
            req.getResource_file().transferTo(file);
        } catch (IOException e) {
            return MsgRespond.fail("内部服务错误，文件上传失败，请稍后再试");
        }
        resourceLessonMapper.insertLessonResource(new ResourceLessonTable(null, lessonId, teacherId, req.getChapter_id(), filePath, fileUtil.getFileSaveDateTime()));
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
        // 保存教材文件
        String filePath = fileUtil.getLessonFilePath(req.getTeacher_id(), req.getLesson_id(), req.getChapter_id(), req.getResource_file());
        // 获取原文件路径
        String oldFilePath = resourceLessonMapper.selectOldFilePath(req.getLesson_id(), req.getTeacher_id(), req.getChapter_id());
        // 保存文件
        try {
            File file = new File(filePath);
            req.getResource_file().transferTo(file);
            // 删除原文件
            File oldFile = new File(oldFilePath);
            if (oldFile.delete()) {
                resourceLessonMapper.updateLessonResourcePath(filePath, fileUtil.getFileSaveDateTime(), req.getLesson_id(), req.getTeacher_id(), req.getChapter_id());
            }
        } catch (IOException e) {
            return MsgRespond.fail("内部服务错误，文件上传失败，请稍后再试");
        }
        return MsgRespond.success("教材资源重传成功");
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
        // 检查教材资源是否存储于服务器中,并删除
        String filePath = resourceLessonMapper.selectOldFilePath(lessonId, teacherId, chapterId);
        File file = new File(filePath);
        if (!file.exists() || file.delete()){
            resourceLessonMapper.deleteOneLessonResource(teacherId, lessonId, chapterId);
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
        if (Objects.isNull(idMark)){
            return MsgRespond.fail("重新检查指定的讲师、课程是否正确");
        }
        // 检查教材文件夹是否存储于服务器中，并删除
        String folderPath = fileUtil.getLessonFolderPath(teacherId, lessonId);
        File file = new File(folderPath);
        if (!file.exists()){
            resourceLessonMapper.deleteAllLessonResource(teacherId, lessonId);
        }else {
            try {
                Files.walk(Path.of(folderPath))
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
                resourceLessonMapper.deleteAllLessonResource(teacherId, lessonId);
            } catch (IOException e) {
                return MsgRespond.fail("内部服务错误，删除失败，请稍后重试");
            }
        }
        return MsgRespond.success("已成功删除此课程教材文件");
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

}
