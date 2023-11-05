package com.training.resource.utils;

import com.training.resource.config.AppConfig;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;

@Component
@AllArgsConstructor
public class FileUtil {
    private final AppConfig appConfig;
    public String getNormalFilePath(Integer upId, MultipartFile file){
        // 获取上传的文件扩展名
        String originalFilename = file.getOriginalFilename();
        String fileExtension = Objects.requireNonNull(originalFilename).substring(originalFilename.lastIndexOf("."));
        // 生成"上传者ID + UUID.后缀"的文件名
        String customFileName = upId.toString() + UUID.randomUUID() + fileExtension;
        // 构建文件保存路径
        return appConfig.getResourceNormalPath() + File.separator + customFileName;
    }

    public String getLessonFilePath(Integer teacherId, Integer lessonId, Integer chapterId, MultipartFile file){
        String originalFilename = file.getOriginalFilename();
        String fileExtension = Objects.requireNonNull(originalFilename).substring(originalFilename.lastIndexOf("."));
        // 生成"课程编号+章节编号+UUID.后缀"的文件名
        String customFileName = '/' + teacherId.toString() + '/' + lessonId + '/' + lessonId + chapterId + UUID.randomUUID() + fileExtension;
        return appConfig.getLessonPath() + File.separator + customFileName;
    }

    private String getFolderDateTime(){
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return currentDateTime.format(formatter);
    }
    public String getFileSaveDateTime(){
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return currentDateTime.format(formatter);
    }

    public String getLessonFolderPath(Integer teacherId, Integer lessonId){
        String customPath = '/' + teacherId.toString() + '/' + lessonId;
        return appConfig.getLessonPath() +  customPath.replace("/", File.separator);
    }
}
