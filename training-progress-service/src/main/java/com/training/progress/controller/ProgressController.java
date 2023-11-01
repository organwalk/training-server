package com.training.progress.controller;

import com.training.common.entity.MsgRespond;
import com.training.progress.servier.ProgressService;
import jakarta.validation.constraints.Digits;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/progress")
public class ProgressController {
    private final ProgressService progressService;

    @PostMapping("/v1/chapter/{lesson_id}/{over_chapter_id}/{student_id}")
    public MsgRespond updateStuChapComplete(@PathVariable
                                            @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "lesson_id必须为纯数字字段")
                                            int lesson_id,
                                            @PathVariable
                                            @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "over_chapter_id必须为纯数字字段")
                                            int over_chapter_id,
                                            @PathVariable
                                            @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "student_id必须为纯数字字段")
                                            int student_id){
        return progressService.MarkChapterComplete(lesson_id,over_chapter_id,student_id);

    }

}
