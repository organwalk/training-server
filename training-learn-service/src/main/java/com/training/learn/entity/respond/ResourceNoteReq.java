package com.training.learn.entity.respond;


import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
public class ResourceNoteReq {
    private Integer lesson_id;
    private Integer chapter_id;
    private Integer up_id;
    private String note_title;
    private String note_des;
    private MultipartFile note_file;
}
