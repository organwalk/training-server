package com.training.plan.controller;

import com.training.common.entity.DataRespond;
import com.training.common.entity.MsgRespond;
import com.training.plan.entity.request.TrainingPlanReq;
import com.training.plan.service.TrainPlanStudentService;
import com.training.plan.service.TrainPlanTeacherService;
import com.training.plan.service.TrainingPlanService;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequestMapping("/api/training")
@AllArgsConstructor
public class TrainingPlanController {
    private final TrainingPlanService trainingPlanService;
    private final TrainPlanStudentService studentService;
    private final TrainPlanTeacherService teacherService;
    //创建计划
    @PostMapping("/v3/plan")
    public MsgRespond creatPlan(@Validated @RequestBody TrainingPlanReq req) throws ParseException {
        return trainingPlanService.creatTrainingPlan(req);
    }
    //添加教师进入计划
    @PostMapping("/v3/plan/teacher/{plan_id}/{teacher_id}")
    public MsgRespond insetTeaInPlan(@PathVariable("plan_id")int plan_id,
                                     @PathVariable("teacher_id")int teacher_id){
        return teacherService.insertTrainPlanTeacher(plan_id,teacher_id);
    }
    //添加学生进入计划
    @PostMapping("/v3/plan/student/{plan_id}/{student_id}")
    public MsgRespond insertStuInPlan(@PathVariable("plan_id")int plan_id,
                                      @PathVariable("student_id")int student_id){
        return studentService.insertTrainPlanStudent(student_id,plan_id);
    }

    @GetMapping("/v3/plan/{page_size}/{offset}")
    public DataRespond getPlanList(@PathVariable
                                   @Min(value = 1, message = "page_size必须为大于1的整数")
                                   @Digits(integer = Integer.MAX_VALUE, fraction = 0)
                                   int page_size,
                                   @PathVariable
                                   @Min(value = 0, message = "offset必须为大于或等于0的整数")
                                   @Digits(integer = Integer.MAX_VALUE, fraction = 0)
                                   int offset){
        return trainingPlanService.getAllPlan(page_size,offset);
    }



}
