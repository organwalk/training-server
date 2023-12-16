package com.training.plan.controller;

import com.training.common.entity.DataRespond;
import com.training.common.entity.MsgRespond;
import com.training.plan.entity.request.*;
import com.training.plan.service.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

/**
 * by zhaozhifeng 2023-10-25
 * by organwalk 2023-11-08
 */
@RestController
@RequestMapping("/api/training")
@AllArgsConstructor
public class TrainingPlanController {
    private final TrainingPlanService trainingPlanService;
    private final TrainPlanStudentService studentService;
    private final TrainPlanTeacherService teacherService;
    private final LessonService lessonService;
    private final ChapterService chapterService;
    private final TestService testService;

    //创建计划
    @PostMapping("/v3/plan")
    public MsgRespond creatPlan(@Validated @RequestBody TrainingPlanReq req) throws ParseException {
        return trainingPlanService.creatTrainingPlan(req);
    }

    //添加教师进入计划
    @PostMapping("/v3/plan/teacher/{plan_id}")
    public MsgRespond insetTeaInPlan(@PathVariable
                                     @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "plan_id必须为纯数字字段")
                                     int plan_id,
                                     @RequestParam("teacherIdList")
                                     @NotEmpty(message = "teacherIdList不能为空")
                                     @Pattern(regexp = "^\\d+$", message = "列表元素必须为整数")
                                     List<Integer> teacherIdList) {
        return teacherService.insertTrainPlanTeacher(plan_id, teacherIdList);
    }

    //添加学生进入计划
    @PostMapping("/v3/plan/student/{plan_id}")
    public MsgRespond insertStuInPlan(@PathVariable
                                      @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "plan_id必须为纯数字字段")
                                      int plan_id,
                                      @RequestParam("studentIdList")
                                      @NotEmpty(message = "studentIdList不能为空")
                                      @Pattern(regexp = "^\\d+$", message = "列表元素必须为整数")
                                      List<Integer> studentIdList) {
        return studentService.insertTrainPlanStudent(studentIdList, plan_id);
    }

    //获取所有计划列表
    @GetMapping("/v3/plan/{page_size}/{offset}")
    public DataRespond getPlanList(@PathVariable
                                   @Min(value = 1, message = "page_size必须为大于1的整数")
                                   @Digits(integer = Integer.MAX_VALUE, fraction = 0)
                                   int page_size,
                                   @PathVariable
                                   @Min(value = 0, message = "offset必须为大于或等于0的整数")
                                   @Digits(integer = Integer.MAX_VALUE, fraction = 0)
                                   int offset) {
        return trainingPlanService.getAllPlan(page_size, offset);
    }

    //获取指定部门的计划列表
    @GetMapping("/v3/plan/dept/{dept_id}/{page_size}/{offset}")
    public DataRespond getDeptPlanList(@PathVariable
                                       @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "dept_id必须为纯数字字段")
                                       Integer dept_id,
                                       @PathVariable
                                       @Min(value = 1, message = "page_size必须为大于1的整数")
                                       @Digits(integer = Integer.MAX_VALUE, fraction = 0)
                                       int page_size,
                                       @PathVariable
                                       @Min(value = 0, message = "offset必须为大于或等于0的整数")
                                       @Digits(integer = Integer.MAX_VALUE, fraction = 0)
                                       int offset) {
        return trainingPlanService.getDeptAllPlan(dept_id, page_size, offset);
    }

    //获取指定计划信息
    @GetMapping("/v1/plan/info/{plan_id}")
    public DataRespond getTrainPlanById(@PathVariable
                                        @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "plan_id必须为纯数字字段")
                                        int plan_id
    ) {
        return trainingPlanService.getTrainPlanById(plan_id);
    }

    //添加指定视频教材测试题
    @PostMapping("/v2/lesson/test/{resource_lesson_id}")
    public MsgRespond addTest(@PathVariable("resource_lesson_id") int lesson_id, @Validated @RequestBody TestReq req) {
        return trainingPlanService.addTest(lesson_id, req);
    }

    //获取指定计划id获取所有的教师信息
    @GetMapping("/v1/plan/teacher/{plan_id}/{page_size}/{offset}")
    public DataRespond getPlanTeacherList(@PathVariable
                                          @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "plan_id必须为纯数字字段")
                                          Integer plan_id,
                                          @PathVariable
                                          @Min(value = 1, message = "page_size必须为大于1的整数")
                                          @Digits(integer = Integer.MAX_VALUE, fraction = 0)
                                          int page_size,
                                          @PathVariable
                                          @Min(value = 0, message = "offset必须为大于或等于0的整数")
                                          @Digits(integer = Integer.MAX_VALUE, fraction = 0)
                                          int offset) {
        return teacherService.getAllTeaByPlanId(plan_id, page_size, offset);
    }

    //获取指定计划id的所有学生信息
    @GetMapping("/v1/plan/student/{plan_id}/{page_size}/{offset}")
    public DataRespond getPlanStuList(@PathVariable
                                      @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "plan_id必须为纯数字字段")
                                      Integer plan_id,
                                      @PathVariable
                                      @Min(value = 1, message = "page_size必须为大于1的整数")
                                      @Digits(integer = Integer.MAX_VALUE, fraction = 0)
                                      int page_size,
                                      @PathVariable
                                      @Min(value = 0, message = "offset必须为大于或等于0的整数")
                                      @Digits(integer = Integer.MAX_VALUE, fraction = 0)
                                      int offset) {
        return studentService.getAllStuByPlanId(plan_id, page_size, offset);
    }

    //编辑指定计划
    @PutMapping("/v3/plan/{plan_id}")
    public MsgRespond updatePlan(@PathVariable
                                 @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "plan_id必须为纯数字字段")
                                 Integer plan_id,
                                 @Validated @RequestBody PlanUpdateReq req
    ) throws ParseException {
        return trainingPlanService.UpdatePlan(plan_id, req);
    }

    //修改培训计划状态
    @PutMapping("/v3/plan/state/{plan_id}/{plan_state}")
    public MsgRespond changeState(@PathVariable
                                  @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "plan_id必须为纯数字字段")
                                  Integer plan_id,
                                  @PathVariable
                                  String plan_state
    ) {
        return trainingPlanService.changeState(plan_state, plan_id);

    }

    //删除指定计划教师
    @DeleteMapping("/v3/plan/teacher/{plan_id}/{teacher_id}")
    public MsgRespond DeleteTea(@PathVariable
                                @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "教师id必须为纯数字字段")
                                int plan_id,
                                @PathVariable
                                @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "教师id必须为纯数字字段")
                                int teacher_id
    ) {
        return teacherService.deleteTea(plan_id, teacher_id);
    }

    //删除指定计划的学生
    @DeleteMapping("/v3/plan/student/{plan_id}/{student_id}")
    public MsgRespond DeleteStu(@PathVariable
                                @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "教师id必须为纯数字字段")
                                int plan_id,
                                @PathVariable
                                @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "教师id必须为纯数字字段")
                                int student_id) {
        return studentService.deleteStu(plan_id, student_id);

    }

    //根据计划id删除计划
    @DeleteMapping("/v3/plan/{plan_id}")
    public MsgRespond DeletePlan(@PathVariable
                                 @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "plan_id必须为纯数字字段")
                                 Integer plan_id
    ) {
        return trainingPlanService.deletePlan(plan_id);

    }

    //添加课程
    @PostMapping("/v2/lesson/{plan_id}")
    public MsgRespond insertLesson(@Validated @RequestBody LessonReq req,
                                   @PathVariable
                                   @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "plan_id必须为纯数字字段")
                                   int plan_id) {
        return lessonService.insertLesson(req, plan_id);
    }

    //获取指定教师的所有课程
    @GetMapping("/v1/lesson/{teacher_id}/{page_size}/{offset}")
    public DataRespond getTeaAllLess(@PathVariable
                                     @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "teacher_id必须为纯数字字段")
                                     Integer teacher_id,
                                     @PathVariable
                                     @Min(value = 1, message = "page_size必须为大于1的整数")
                                     @Digits(integer = Integer.MAX_VALUE, fraction = 0)
                                     int page_size,
                                     @PathVariable
                                     @Min(value = 0, message = "offset必须为大于或等于0的整数")
                                     @Digits(integer = Integer.MAX_VALUE, fraction = 0)
                                     int offset) {
        return lessonService.getTeaAllLess(teacher_id, page_size, offset);
    }

    //通过课程id获取指定课程
    @GetMapping("/v1/lesson/info/{lesson_id}")
    public DataRespond getLessInfoById(@PathVariable
                                       @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "lesson_id必须为纯数字字段")
                                       Integer lesson_id) {
        return lessonService.getLessInfoById(lesson_id);
    }

    //编辑指定教师和课程的课程详细信息
    @PutMapping("/v2/lesson/info/{teacher_id}/{lesson_id}")
    public MsgRespond updateLess(@PathVariable
                                 @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "teacher_id必须为纯数字字段")
                                 Integer teacher_id,
                                 @PathVariable
                                 @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "lesson_id必须为纯数字字段")
                                 Integer lesson_id,
                                 @Validated @RequestBody LessonUpdate req
    ) {
        return lessonService.updateLesson(req, lesson_id, teacher_id);
    }

    //删除指定教师的指定课程
    @DeleteMapping("/v2/lesson/{lesson_id}")
    public MsgRespond DeleteLessonById(@PathVariable
                                       @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "lesson_id必须为纯数字字段")
                                       Integer lesson_id) {
        return lessonService.DeleteLessonById(lesson_id);
    }

    //删除指定教师的所有课程
    @DeleteMapping("/v4/lesson/teacher/{teacher_id}")
    public MsgRespond DeleteTAllLesson(@PathVariable
                                       @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "lesson_id必须为纯数字字段")
                                       Integer teacher_id) {
        return lessonService.DeleteLessonByTId(teacher_id);
    }

    //插入指定课程的章节
    @PostMapping("/v2/lesson/chapter/{lesson_id}")
    public MsgRespond insertChapter(@PathVariable
                                    @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "lesson_id必须为纯数字字段")
                                    Integer lesson_id, @RequestBody String chapter_name) {
        return chapterService.insertChapter(chapter_name, lesson_id);
    }

    //获取指定课程的所有章节
    @GetMapping("/v1/lesson/chapter/{lesson_id}")
    public DataRespond getChapterByLID(@PathVariable
                                       @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "lesson_id必须为纯数字字段")
                                       Integer lesson_id) {
        return chapterService.getChapterLessonId(lesson_id);
    }

    //编辑指定章节
    @PutMapping("/v2/lesson/chapter/{chapter_id}")
    public MsgRespond updateChapterName(@PathVariable
                                        @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "chapter_id必须为纯数字字段")
                                        Integer chapter_id, @RequestBody String chapter_name) {
        return chapterService.updateChapterName(chapter_name, chapter_id);
    }

    //删除指定章节
    @DeleteMapping("/v2/lesson/chapter/{chapter_id}")
    public MsgRespond deleteChapterById(@PathVariable
                                        @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "chapter_id必须为纯数字字段")
                                        Integer chapter_id) {
        return chapterService.deleteChapterById(chapter_id);
    }

    //删除指定课程下的所有章节
    @DeleteMapping("/v2/lesson/chapter/teacher/{teacher_id}")
    public MsgRespond deleteAllChapterByLessonId(@PathVariable
                                                 @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "chapter_id必须为纯数字字段")
                                                 Integer teacher_id) {
        return chapterService.deleteAllChapterByLessonId(teacher_id);
    }

    //设定课程状态为发布
    @PutMapping("/v2/lesson/state/{lesson_id}")
    public MsgRespond updateState(@PathVariable
                                  @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "lesson_id必须为纯数字字段")
                                  Integer lesson_id) {
        return lessonService.updateState(lesson_id);
    }

    //获取指定视频教材的所有测试
    @GetMapping("/v1/lesson/test/{resource_lesson_id}")
    public DataRespond getAllTestByRLId(@PathVariable
                                        @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "resource_lesson_id必须为纯数字字段")
                                        Integer resource_lesson_id
    ) {
        return testService.getAllTestByRLId(resource_lesson_id);
    }

    //根据id获取指定测试
    @GetMapping("/v1/lesson/test/info/{test_id}")
    public DataRespond getTestById(@PathVariable
                                   @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "test_id必须为纯数字字段")
                                   Integer test_id) {
        return testService.getTestById(test_id);
    }

    //根据id删除指定测试
    @DeleteMapping("/v2/lesson/test/{test_id}")
    public MsgRespond deleteTestById(@PathVariable
                                     @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "test_id必须为纯数字字段")
                                     Integer test_id) {
        return testService.deleteById(test_id);
    }

    //删除指定课程教材下的测试
    @DeleteMapping("/v4/lesson/test/resource/{resource_lesson_id}")
    public MsgRespond deleteByRLId(@PathVariable
                                   @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "resource_lesson_id必须为纯数字字段")
                                   Integer resource_lesson_id) {
        return testService.deleteAllTest(resource_lesson_id);
    }

    //编辑测试题
    @PutMapping("/v2/lesson/test/info/{test_id}")
    public MsgRespond updateTest(@PathVariable
                                 @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "test_id必须为纯数字字段")
                                 Integer test_id, @Validated @RequestBody TestReq req) {
        return testService.updateTest(req, test_id);
    }

    // 模糊搜索培训计划
    @GetMapping("/v3/plan/keyword/{keyword}/{page_size}/{offset}")
    public DataRespond getAllPlanByKeyword(@PathVariable @NotBlank(message = "keyword不能为空") String keyword,
                                           @PathVariable
                                           @Min(value = 1, message = "page_size必须为大于1的整数")
                                           @Digits(integer = Integer.MAX_VALUE, fraction = 0)
                                           int page_size,
                                           @PathVariable
                                           @Min(value = 0, message = "offset必须为大于或等于0的整数")
                                           @Digits(integer = Integer.MAX_VALUE, fraction = 0)
                                           int offset) {
        return trainingPlanService.getAllPlanByKeyword(keyword, page_size, offset);
    }

    // 获取指定讲师所处的培训计划列表
    @GetMapping("/v2/teacher/plan/{teacher_id}")
    public DataRespond getPlanListByTeacherId(@PathVariable Integer teacher_id){
        return teacherService.getPlanListByTeacher(teacher_id);
    }

    // 获取指定学员所处的培训计划列表
    @GetMapping("/v1/student/plan/{student_id}")
    public DataRespond getPlanListByStudentId(@PathVariable Integer student_id){
        return studentService.getPlanListByStudent(student_id);
    }


}
