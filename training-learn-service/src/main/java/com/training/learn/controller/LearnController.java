package com.training.learn.controller;

import com.training.common.entity.DataRespond;
import com.training.learn.service.TrainingService;
import jakarta.validation.constraints.Digits;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/learn")
@AllArgsConstructor
public class LearnController {

    private final TrainingService trainingService;


    @GetMapping("/v1/plan/{student_id}/{page_size}/{offset}")
    public DataRespond getPlanByStuId(@PathVariable
                                      @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "student_id必须为纯数字字段")
                                      int student_id,
                                      @PathVariable
                                      @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "page_size必须为纯数字字段")
                                      int page_size,
                                      @PathVariable
                                      @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "offset必须为纯数字字段")
                                      int offset){
        return trainingService.getPlanListByStuId(student_id,page_size,offset);
    }
}
