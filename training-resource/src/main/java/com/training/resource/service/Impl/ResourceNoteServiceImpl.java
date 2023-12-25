package com.training.resource.service.Impl;

import com.training.common.entity.DataFailRespond;
import com.training.common.entity.DataRespond;
import com.training.common.entity.DataSuccessRespond;
import com.training.common.entity.MsgRespond;
import com.training.resource.entity.request.ResourceNoteReq;
import com.training.resource.entity.table.ResourceNoteTable;
import com.training.resource.mapper.ResourceNoteMapper;
import com.training.resource.service.ResourceNoteService;
import com.training.resource.utils.DataUtil;
import com.training.resource.utils.FileResUtil;
import com.training.resource.utils.FileUtil;
import lombok.AllArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Objects;

/**
 * 定义学习笔记相关业务接口的具体实现
 * by organwalk 2023-10-21
 */
@Service
@AllArgsConstructor
@Transactional
public class ResourceNoteServiceImpl implements ResourceNoteService {
    private final ResourceNoteMapper resourceNoteMapper;
    private final DataUtil dataUtil;
    private final FileUtil fileUtil;
    private final FileResUtil fileResUtil;

    /**
     * 上传笔记具体实现
     * @param req 笔记请求实体
     * @return 根据处理结果返回提示消息，成功时，其data会返回笔记ID
     * by organwalk 2023-11-06
     */
    @Override
    public DataRespond uploadNote(ResourceNoteReq req) {
        String originName = req.getNote_file().getOriginalFilename();
        String fileExtension = Objects.requireNonNull(originName).substring(originName.lastIndexOf("."));
        if (!Objects.equals(fileExtension, ".md")){
            return new DataFailRespond("仅支持上传md类型文件至云端");
        }
        Integer lessonId = req.getLesson_id();
        Integer chapterId = req.getChapter_id();
        // 检查章节与课程一致性
        String chapterMark = dataUtil.validChapter(lessonId, chapterId);
        if (Objects.nonNull(chapterMark)){
            return new DataFailRespond(chapterMark);
        }
        // 保存教材文件
        String filePath = fileUtil.getNoteFilePath(lessonId, chapterId, req.getUp_id(), originName);
        // 保存文件
        try {
            File file = new File(filePath);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs(); // 创建路径中所有不存在的目录
            }
            req.getNote_file().transferTo(file);
        } catch (IOException e) {
            return new DataFailRespond("内部服务错误，笔记上传失败，请稍后再试");
        }
        ResourceNoteTable resourceNoteTable = new ResourceNoteTable(null, lessonId, chapterId, req.getUp_id(), req.getNote_title(), req.getNote_des(), filePath, fileUtil.getFileSaveDateTime());
        resourceNoteMapper.insertNote(resourceNoteTable);
        return new DataSuccessRespond("已成功上传至云端", resourceNoteTable.getId());
    }

    /**
     * 删除一份笔记
     * @param uid 上传用户ID
     * @param noteId 笔记ID
     * @param username 用户名
     * @param auth 权限
     * @return 根据处理结果返回提示消息
     * by organwalk 2023-11-06
     */
    @Override
    public MsgRespond deleteOneNoteByUser(Integer uid, Integer noteId, String username, String auth) {
        String userMark = dataUtil.checkResourceAuth(uid, auth, username);
        if (!userMark.isBlank()){
            return MsgRespond.fail(userMark);
        }
        String notePath = resourceNoteMapper.selectNotePathById(noteId);
        if (Objects.isNull(notePath)){
            return MsgRespond.fail("该笔记不存在");
        }
        File file = new File(notePath);
        if (!file.exists() || file.delete()){
            resourceNoteMapper.deleteNoteById(noteId);
        }else {
            return MsgRespond.fail("文件处理异常，请稍后再试");
        }
        return MsgRespond.success("已成功删除此笔记");
    }

    /**
     * 删除指定章节的学习笔记具体实现
     * @param chapterId 章节ID
     * @return 根据处理结果返回消息
     * by organwalk 2023-11-07
     */
    @Override
    public MsgRespond deleteNoteByChapter(Integer lessonId, Integer chapterId) {
        Integer idMark = resourceNoteMapper.selectNoteIdCountByChapterId(chapterId);
        if (idMark == 0){
            return MsgRespond.fail("指定章节不存在笔记");
        }
        String noteFolderPath = fileUtil.getNoteChapterFolderPath(lessonId, chapterId);
        File file = new File(noteFolderPath);
        if (!file.exists()){
            resourceNoteMapper.deleteNoteByChapterId(chapterId);
        }else {
            try {
                Files.walk(Path.of(noteFolderPath))
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
                resourceNoteMapper.deleteNoteByChapterId(chapterId);
            } catch (IOException e) {
                return MsgRespond.fail("内部服务错误，删除失败，请稍后重试");
            }
        }
        return MsgRespond.success("已成功删除此章节下笔记");
    }

    @Override
    public MsgRespond deleteNoteByLesson(Integer lessonId) {
        Integer idMark = resourceNoteMapper.selectNoteIdCountByLessonId(lessonId);
        if (idMark == 0) {
            return MsgRespond.fail("指定课程下不存在笔记");
        }
        String noteFolderPath = fileUtil.getNoteLessonFolderPath(lessonId);
        File file = new File(noteFolderPath);
        if (!file.exists()){
            resourceNoteMapper.deleteNoteByLessonId(lessonId);
        }else {
            try {
                Files.walk(Path.of(noteFolderPath))
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
                resourceNoteMapper.deleteNoteByLessonId(lessonId);
            } catch (IOException e) {
                return MsgRespond.fail("内部服务错误，删除失败，请稍后重试");
            }
        }
        return MsgRespond.success("已成功删除此课程下笔记");
    }

    @Override
    public ResponseEntity<?> getNoteById(Integer noteId) {
        String notePath = resourceNoteMapper.selectNotePathById(noteId);
        return fileResUtil.returnMarkdown(notePath);
    }

    @Override
    public DataRespond getNoteDetail(Integer noteId) {
        ResourceNoteTable noteDetail = resourceNoteMapper.selectNoteDetail(noteId);
        if (Objects.isNull(noteDetail)){
            return new DataFailRespond("fail");
        }
        return new DataSuccessRespond("success", noteDetail);
    }


}
