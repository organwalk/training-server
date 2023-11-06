package com.training.progress.controller;

import com.training.common.entity.DataRespond;
import com.training.common.entity.MsgRespond;
import com.training.progress.service.ProgressService;
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
                                            int student_id) {
        return progressService.MarkChapterComplete(lesson_id, over_chapter_id, student_id);
    }





    @PostMapping("/v2/lesson/student/{lesson_id}/{student_id}/{over_chapter_sum}/{lesson_chapter_sum}")
    public MsgRespond updateStuLessonProgress(@PathVariable
                                              @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "lesson_id必须为纯数字字段")
                                              int lesson_id,
                                              @PathVariable
                                              @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "student_id必须为纯数字字段")
                                              int student_id,
                                              @PathVariable
                                              @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "over_chapter_sum必须为纯数字字段")
                                              int over_chapter_sum,
                                              @PathVariable
                                              @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "lesson_chapter_sum必须为纯数字字段")
                                              int lesson_chapter_sum
    ) {
        return progressService.insertStuLessonProgress(lesson_id, student_id, over_chapter_sum, lesson_chapter_sum);
    }




    @GetMapping("/v2/lesson/student/chapter/{lesson_id}/{page_size}/{offset}")
    public DataRespond getLastChapter(@PathVariable
                                      @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "lesson_id必须为纯数字字段")
                                      int lesson_id,
                                      @PathVariable
                                      @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "page_size必须为纯数字字段")
                                      int page_size,
                                      @PathVariable
                                      @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "offset必须为纯数字字段")
                                      int offset) {
        return progressService.getStuAllByLessonId(lesson_id, page_size, offset);
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
                                              Integer offset) {
        return progressService.getAllPlanPresent(page_size,offset);
    }




    //获取学员在指定课程下学习进度百分比
    @GetMapping("/v2/lesson/student/chapter/persent/{lesson_id}/{page_size}/{offset}")
    public DataRespond getAllStuPresent(@PathVariable
                                            @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "lesson_id必须为纯数字字段")
                                            int lesson_id,
                                        @PathVariable
                                            @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "page_size必须为纯数字字段")
                                            int page_size,
                                        @PathVariable
                                            @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "offset必须为纯数字字段")
                                            int offset){
        return progressService.getAllStuLessonPresent(lesson_id,page_size,offset);
    }




    //建立课程进度跟踪机制
    @PostMapping("/v2/plan/lesson/teacher/{plan_id}/{teacher_id}/{lesson_id}")
    public MsgRespond LessonTrack(@PathVariable
                                      @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "plan_id必须为纯数字字段")
                                      int plan_id,
                                  @PathVariable
                                  @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "teacher_id必须为纯数字字段")
                                  int teacher_id,
                                  @PathVariable
                                      @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "lesson_id必须为纯数字字段")
                                      int lesson_id){
            return progressService.insertProgressPlan(plan_id,lesson_id,teacher_id);
    }




    @GetMapping("/v4/lesson/teacher/persent/{teacher_id}")
    public DataRespond getTeaPresent(@PathVariable
                                     @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "teacher_id必须为纯数字字段")
                                     int teacher_id,
                                     @RequestHeader("auth")String auth,
                                     @RequestHeader("username")String username) {
        return progressService.getTeaAllPresent(teacher_id,auth,username);
    }









    //获取指定培训计划下建立跟踪机制的课程ID列表
    @GetMapping("/v1/plan/lesson/{plan_id}/{page_size}/{offset}")
    public DataRespond getLessonIdListByPlanId(@PathVariable
                                               @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "plan_id必须为纯数字字段")
                                               int plan_id,
                                               @PathVariable
                                               @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "page_size必须为纯数字字段")
                                               int page_size,
                                               @PathVariable
                                               @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "offset必须为纯数字字段")
                                               int offset){
        return progressService.gerLessonIdListByPlanId(plan_id,page_size,offset);
    }






    //获取指定培训计划下指定学员的课程进度百分比列表
    @GetMapping("/v1/plan/lesson/persent/{plan_id}/{student_id}/{page_size}/{offset}")
    public DataRespond getLessonByPIdAndStuId(@PathVariable
                                                  @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "plan_id必须为纯数字字段")
                                                  int plan_id,
                                              @PathVariable
                                              @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "student_id必须为纯数字字段")
                                              int student_id,
                                              @PathVariable
                                                  @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "page_size必须为纯数字字段")
                                                  int page_size,
                                              @PathVariable
                                                  @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "offset必须为纯数字字段")
                                                  int offset){
        return progressService.getAllLessonPresentByStuId(student_id,plan_id,page_size,offset);
    }


    // 获取指定学员课程章节的所学进度及百分比列表
    @GetMapping("/v1/plan/lesson/chapter/persent/{lesson_id}/{student_id}")
    public DataRespond getChapterByLIdAndSId(@PathVariable
                                             @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "lesson_id必须为纯数字字段")
                                             int lesson_id,
                                             @PathVariable
                                             @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "student_id必须为纯数字字段")
                                             int student_id){
        return progressService.getChapterListByStuIdAndLessonId(student_id,lesson_id);

    }



}
