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
public class MP4BoxUtil {
    private static final Logger logger = LogManager.getLogger(MP4BoxUtil.class);

    public boolean processMP4ToFMP4(String filePath){

        // mp4Box -dash 4000 filePath.mp4 -out output_file_name.mpd
        String outputName = UUID.randomUUID() + ".mp4";
        // 命令参数
        String[] mp4boxCommand = {
                "mp4box",
                "-dash",
                "4000",
                filePath,
                "-out",
                outputName
        };
        logger.info("正在使用Mp4box进行视频处理：" + Arrays.toString(mp4boxCommand));
        try {
            // 创建 ProcessBuilder 对象
            ProcessBuilder processBuilder = new ProcessBuilder(mp4boxCommand);

            logger.info("启动线程并执行命令");
            Process process = processBuilder.start();

            logger.info("等待命令完成");
            int exitCode = process.waitFor();

            File originalFile = new File(filePath);
            // 删除原始文件
            deleteFile(originalFile);

            if (exitCode == 0) {
                File processedFile = new File(outputName);
                if (processedFile.exists()) {
                    File newFileName = new File(filePath);
                    if (processedFile.renameTo(newFileName)){
                        logger.info("重命名处理文件");
                    }
                }
                logger.info("视频处理成功");
                return true;
            } else {
                logger.info("视频处理失败");
                return false;
            }
        } catch (IOException | InterruptedException e) {
            logger.error(e);
            return false;
        }
    }

    private void deleteFile(File file){
        if (file.exists() && file.delete()) {
            logger.info("删除原始文件");
        }
    }
}
