package com.training.resource.utils;

import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

@Component
@AllArgsConstructor
public class FfmpegUtil {
    private static final Logger logger = LogManager.getLogger(FfmpegUtil.class);

    public boolean processMP4ToFMP4(String filePath) throws IOException {
        String outputName = UUID.randomUUID() + ".mp4";
        // 命令参数
        String[] ffmpegCommand = {
                "ffmpeg",
                "-i",
                filePath,
                "-c:v",
                "copy",
                "-c:a",
                "copy",
                "-movflags",
                "frag_keyframe+empty_moov",
                outputName
        };
        logger.info("正在使用ffmpeg进行视频处理，命令为：" + Arrays.toString(ffmpegCommand));
        try {
            // 创建 ProcessBuilder 对象
            ProcessBuilder processBuilder = new ProcessBuilder(ffmpegCommand);

            // 设置工作目录（如果需要的话）
            // processBuilder.directory(new File("path/to/ffmpeg/directory"));

            logger.info("启动线程并执行命令");
            Process process = processBuilder.start();

            logger.info("等待命令完成");
            int exitCode = process.waitFor();

            // 如果命令成功执行（exitCode = 0）
            if (exitCode == 0) {
                logger.info("成功执行命令，删除原始文件");
                File originalFile = new File(filePath);
                if (originalFile.exists()) {
                    originalFile.delete();
                }
                logger.info("重命名处理文件");
                File processedFile = new File(outputName);
                if (processedFile.exists()) {
                    File newFileName = new File(filePath);
                    processedFile.renameTo(newFileName);
                }
                logger.info("视频处理成功");
                return true;
            } else {
                logger.info("视频处理失败");
                // 处理命令执行失败的情况
                return false;
            }
        } catch (IOException | InterruptedException e) {
            logger.error(e);
            return false;
        }
    }
}
