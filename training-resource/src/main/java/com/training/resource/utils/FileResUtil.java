package com.training.resource.utils;

import com.training.common.entity.MsgRespond;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

@Component
public class FileResUtil {
    public ResponseEntity<?> returnMarkdown(String lessonFilePath) {
        byte[] markdownBytes;
        try {
            markdownBytes = Files.readAllBytes(Paths.get(lessonFilePath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_MARKDOWN);
        headers.set(HttpHeaders.CONTENT_ENCODING, "utf-8");
        headers.set(HttpHeaders.CONTENT_TYPE, "text/markdown; charset=UTF-8");
        return ResponseEntity.ok()
                .headers(headers)
                .body(new String(markdownBytes, StandardCharsets.UTF_8));
    }

    public ResponseEntity<?> returnMsg(String type, String msg){
        if (Objects.equals(type, "success")){
            return ResponseEntity.status(HttpStatus.OK).body(MsgRespond.success(msg));
        }else {
            return ResponseEntity.status(HttpStatus.OK).body(MsgRespond.fail(msg));
        }
    }

    public ResponseEntity<?> returnVideo(String rangeString,
                                               String lessonFilePath) throws IOException {
        File file = new File(lessonFilePath);
        long fileLength = file.length();
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");

        long rangeStart = 0;
        if (rangeString != null) {
            rangeStart = Long.parseLong(rangeString.substring(rangeString.indexOf("=") + 1, rangeString.indexOf("-")));
        }

        randomAccessFile.seek(rangeStart);
        byte[] bytes = new byte[1024 * 1024];
        int len = randomAccessFile.read(bytes);
        randomAccessFile.close();

        String contentRange = "bytes " + rangeStart + "-" + (rangeStart + len - 1) + "/" + fileLength;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("video/mp4"));
        headers.setContentLength(len);
        headers.set("Content-Range", contentRange);
        HttpStatus status = HttpStatus.PARTIAL_CONTENT;

        return new ResponseEntity<>(bytes, headers, status);
    }
}
