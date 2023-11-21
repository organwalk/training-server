package com.training.resource.utils;

import com.training.resource.config.AppConfig;
import com.training.resource.exceptions.GlobalExceptionHandler;
import com.training.resource.mapper.ResourceLessonMapper;
import com.training.resource.mapper.ResourceNormalMapper;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
@AllArgsConstructor
public class FileUtil {
    private final AppConfig appConfig;
    private final ResourceNormalMapper resourceNormalMapper;
    private final ResourceLessonMapper resourceLessonMapper;
    private final FfmpegUtil ffmpegUtil;
    private static final Logger logger = LogManager.getLogger(FileUtil.class);
    private static final ConcurrentMap<String, File> SHA_CACHE = new ConcurrentHashMap<>();
    public String chunkSaveFile(String hashValue,
                                String filePath,
                                Integer fileChunksSum,
                                Integer fileNowChunk,
                                Integer fileSize,
                                MultipartFile multipartFile) throws IOException {
        File targetFile = SHA_CACHE.get(hashValue);
        if (targetFile == null) {
            targetFile = new File(filePath);
            File parentDir = targetFile.getParentFile();
            if (!parentDir.exists()) {
                if (parentDir.mkdirs()) {
                    SHA_CACHE.put(hashValue, targetFile);
                } else {
                    return "文件处理错误，请稍后再试";
                }
            } else {
                SHA_CACHE.put(hashValue, targetFile);
            }
        }
        // 对文件的任意位置进行读写
        RandomAccessFile accessFile = new RandomAccessFile(targetFile, "rw");
        boolean finished = Objects.equals(fileNowChunk, fileChunksSum);//是否最后一片
        if (finished) {
            accessFile.seek(fileSize - multipartFile.getSize());
        }else {
            accessFile.seek((fileNowChunk - 1) * multipartFile.getSize());
        }
        // 写入分片的数据
        accessFile.write(multipartFile.getBytes());
        accessFile.close();
        if (finished) {
            logger.info("已完成分片上传，移除分片缓存");
            SHA_CACHE.remove(hashValue);
            logger.info("开始将MP4转换为FMP4");
            boolean process = ffmpegUtil.processMP4ToFMP4(filePath);
            return process ? "true" : null;
        }
        return null;
    }
    public String getNormalFilePath(Integer upId, String fileOriginName) {
        // 获取上传的文件扩展名
        String fileExtension = Objects.requireNonNull(fileOriginName).substring(fileOriginName.lastIndexOf("."));
        // 生成"上传者ID + UUID.后缀"的文件名
        String customFileName = getFolderDateTime() + File.separator + upId.toString() + UUID.randomUUID() + fileExtension;
        // 构建文件保存路径
        return appConfig.getResourceNormalPath() + customFileName;
    }

    public String getLessonFilePath(Integer teacherId, Integer lessonId, Integer chapterId, String fileOriginName){
        String fileExtension = Objects.requireNonNull(fileOriginName).substring(fileOriginName.lastIndexOf("."));
        // 生成"课程编号+章节编号+UUID.后缀"的文件名
        String customFileName = teacherId.toString() + File.separator + lessonId + File.separator + lessonId + chapterId + UUID.randomUUID() + fileExtension;
        return appConfig.getLessonPath() + customFileName;
    }

    public String getNoteFilePath(Integer lessonId, Integer chapterId, Integer upId, String fileOriginName){
        // 获取上传的文件扩展名
        String fileExtension = Objects.requireNonNull(fileOriginName).substring(fileOriginName.lastIndexOf("."));
        // 生成"上传者ID + UUID.后缀"的文件名
        String customFileName = lessonId.toString() + '/' + chapterId  + '/' + lessonId + chapterId + upId + UUID.randomUUID() + fileExtension;
        // 构建文件保存路径
        return appConfig.getNotePath() + File.separator + customFileName;
    }

    // 检查是否具有相同哈希值的文件
    public String checkEqualHashFilePath(String type, String hash){
        if (Objects.equals(type, "normal")){
            return resourceNormalMapper.selectPathByFileHash(hash);
        }else if (Objects.equals(type, "lesson")){
            return resourceLessonMapper.selectPathByFileHash(hash);
        }
        return "";
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

    public String getNoteChapterFolderPath(Integer lessonId, Integer chapterId){
        String customPath = '/' + lessonId.toString() + '/' + chapterId;
        return appConfig.getNotePath() + customPath.replace("/", File.separator);
    }

    public String getNoteLessonFolderPath(Integer lessonId){
        String customPath = '/' + lessonId.toString();
        return appConfig.getNotePath() + customPath.replace("/", File.separator);
    }

    private String getFolderDateTime(){
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return currentDateTime.format(formatter);
    }
}
