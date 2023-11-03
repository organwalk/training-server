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
import com.training.resource.utils.FileUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
@AllArgsConstructor
public class ResourceLessonImpl implements ResourceLessonService {

    private final ResourceLessonMapper resourceLessonMapper;
    private final TrainingClient trainingClient;
    private final FileUtil fileUtil;
    /**
     * 上传教材资源文件具体实现
     * @param req 教材对象
     * @return 根据处理结果返回消息提示
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
        String chapterMark = validChapter(lessonId, req.getChapter_id());
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

    private String validChapter(Integer lessonId, Integer chapterId){
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
}
