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
import com.training.resource.utils.FileUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

@Service
@AllArgsConstructor
@Transactional
public class ResourceNoteServiceImpl implements ResourceNoteService {
    private final ResourceNoteMapper resourceNoteMapper;
    private final DataUtil dataUtil;
    private final FileUtil fileUtil;

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
        }
        return MsgRespond.success("已成功删除此笔记");
    }


}
