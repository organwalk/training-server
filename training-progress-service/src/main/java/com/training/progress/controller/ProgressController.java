package com.training.progress.controller;

import com.training.common.entity.DataRespond;
import com.training.common.entity.MsgRespond;
import com.training.progress.servier.ProgressService;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

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


    //获取培训计划进度列表()开发中
    @GetMapping("/v3/plan/persent/{page_size}/{offset}")
    public DataRespond getAllPlanProgressList(@PathVariable
                                                  @Min(value = 1, message = "page_size必须为大于1的整数")
                                                  @Digits(integer = Integer.MAX_VALUE, fraction = 0)
                                                  Integer page_size,
                                              @PathVariable
                                                  @Min(value = 0, message = "offset必须为大于或等于0的整数")
                                                  @Digits(integer = Integer.MAX_VALUE, fraction = 0)
                                                  Integer offset){
        return progressService.getAllPlanProgressList(page_size,offset);
    }


}
