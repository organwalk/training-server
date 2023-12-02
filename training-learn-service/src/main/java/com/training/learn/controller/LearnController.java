package com.training.learn.controller;

import com.training.common.entity.DataRespond;
import com.training.common.entity.MsgRespond;
import com.training.learn.entity.request.*;

import com.training.learn.service.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequestMapping("/api/learn")
@AllArgsConstructor
public class LearnController {

    private final TrainingService trainingService;
    private final CommentService commentService;
    private final ReplyService replyService;
    private final LikeService likeService;
    private final TestService testService;

    // 获取指定学员所具有的培训计划列表
    @GetMapping("/v1/plan/{student_id}/{page_size}/{offset}")
    public DataRespond getPlanByStuId(@PathVariable
                                      @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "student_id必须为纯数字字段")
                                      int student_id,
                                      @PathVariable
                                      @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "page_size必须为纯数字字段")
                                      int page_size,
                                      @PathVariable
                                      @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "offset必须为纯数字字段")
                                      int offset) {
        return trainingService.getPlanListByStuId(student_id, page_size, offset);
    }


    // 获取指定培训计划的课程列表
    @GetMapping("/v1/plan/lesson/{plan_id}/{student_id}/{page_size}/{offset}")
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
                                              int offset) {
        return trainingService.getLessonByPIdAndStuId(plan_id, student_id, page_size, offset);
    }


    //获取指定课程下的章节列表
    @GetMapping("/v1/plan/lesson/chapter/{lesson_id}/{student_id}")
    public DataRespond getChapterByLessIdAndStuId(@PathVariable
                                                  @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "student_id必须为纯数字字段")
                                                  int student_id,
                                                  @PathVariable
                                                  @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "plan_id必须为纯数字字段")
                                                  int lesson_id) {
        return trainingService.getChapterByStuIdAndLessId(student_id, lesson_id);
    }


    //指定课程发布评论
    @PostMapping("/v1/lesson/comment")
    public MsgRespond insertLessonContent(@RequestBody String request) {
        return commentService.insertCommentOne(request);
    }


    //指定课程章节下发布评论
    @PostMapping("/v1/lesson/chapter/comment")
    public MsgRespond insertChapterContent(@RequestBody String request) {
        return commentService.insertCommentTwo(request);
    }


    //指定评论发表回复
    @PostMapping("/v1/lesson/reply")
    public MsgRespond replyComment(@RequestBody String request) {
        return replyService.ReplyComment(request);
    }


    //指定跟帖回复进行再跟帖
    @PostMapping("/v1/lesson/reply/thread")
    public MsgRespond ThreadReply(@RequestBody String request) {
        return replyService.ThreadReply(request);
    }


    //指定跟帖回复进行点赞/取消点赞
    @PostMapping("/v1/comment/like/{user_id}/{comment_id}/{state}")
    public MsgRespond CommentLikeOrDisLike(@PathVariable
                                           @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "用户id必须为纯数字字段")
                                           int user_id,
                                           @PathVariable
                                           @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "评论id必须为纯数字字段")
                                           int comment_id,
                                           @PathVariable
                                           @Min(value = 0, message = "lesson_state只能为0或1")
                                           @Max(value = 1, message = "lesson_state只能为0或1")
                                           int state
    ) {
        return likeService.LikeComment(user_id, comment_id, state);
    }


    //获取课程的评论列表
    @GetMapping("/v1/comment/lesson/{lesson_id}/{user_id}/{page_size}/{offset}")
    public DataRespond getCommentList(@PathVariable
                                      @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "lesson_id必须为纯数字字段")
                                      int lesson_id,
                                      @PathVariable
                                      @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "user_id必须为纯数字字段")
                                      int user_id,
                                      @PathVariable
                                      @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "page_size必须为纯数字字段")
                                      int page_size, @PathVariable
                                      @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "offset必须为纯数字字段")
                                      int offset) {
        return replyService.getCommentList(lesson_id, user_id, page_size, offset);
    }


    //获取主评论的跟帖回复列表
    @GetMapping("/v1/reply/{comment_id}/{user_id}/{page_size}/{offset}")
    public DataRespond getReplyList(@PathVariable
                                    @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "comment_id必须为纯数字字段")
                                    int comment_id,
                                    @PathVariable
                                    @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "user_id必须为纯数字字段")
                                    int user_id,
                                    @PathVariable
                                    @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "page_size必须为纯数字字段")
                                    int page_size, @PathVariable
                                    @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "offset必须为纯数字字段")
                                    int offset) {
        return replyService.getReplyList(comment_id, user_id, page_size, offset);
    }


    //获取课程章节的评论列表
    @GetMapping("/v1/comment/lesson/chapter/{lesson_id}/{chapter_id}/{user_id}/{page_size}/{offset}")
    public DataRespond getCommentListByLessonIdAndChapterId(@PathVariable
                                                            @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "lesson_id必须为纯数字字段")
                                                            int lesson_id,
                                                            @PathVariable
                                                            @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "chapter_id必须为纯数字字段")
                                                            int chapter_id,
                                                            @PathVariable
                                                            @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "user_id必须为纯数字字段")
                                                            int user_id,
                                                            @PathVariable
                                                            @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "page_size必须为纯数字字段")
                                                            int page_size, @PathVariable
                                                            @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "offset必须为纯数字字段")
                                                            int offset) {
        return replyService.getCommentListByLessonIdAndChapterId(lesson_id, chapter_id, user_id, page_size, offset);
    }


    //指定跟帖回复进行点赞/取消点赞
    @PostMapping("/v1/reply/like/{user_id}/{reply_id}/{state}")
    public MsgRespond LikeReplyOrDisLike(@PathVariable
                                         @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "user_id必须为纯数字字段")
                                         int user_id,
                                         @PathVariable
                                         @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "reply_id必须为纯数字字段")
                                         int reply_id,
                                         @PathVariable
                                         @Min(value = 0, message = "lesson_state只能为0或1")
                                         @Max(value = 1, message = "lesson_state只能为0或1")
                                         int state) {
        return likeService.LikeReply(user_id, reply_id, state);
    }


    //删除指定主评论
    @DeleteMapping("/v1/comment/{comment_id}")
    public MsgRespond DeleteComemt(@PathVariable
                                   @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "comment_id必须为纯数字字段")
                                   int comment_id) {
        return commentService.DeleteComment(comment_id);
    }


    //删除指定跟帖回复
    @DeleteMapping("/v1/reply/{reply_id}")
    public MsgRespond DeletReply(@PathVariable
                                 @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "reply_id必须为纯数字字段")
                                 int reply_id) {
        return replyService.deleteReply(reply_id);
    }


    //发布学习笔记
    @PostMapping("/v1/lesson/chaper/comment/note/{user_id}/{lesson_id}/{chapter_id}")
    public MsgRespond insertNote(@PathVariable
                                 @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "user_id必须为纯数字字段")
                                 Integer user_id,
                                 @PathVariable
                                 @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "lesson_id必须为纯数字字段")
                                 Integer lesson_id,
                                 @PathVariable
                                 @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "chapter_id必须为纯数字字段")
                                 Integer chapter_id,
                                 @Validated @RequestBody NoteReq req) {
        return commentService.insertNote(user_id, lesson_id, chapter_id, req);
    }

    // 获取主评论内容
    @GetMapping("/v1/comment/{comment_id}")
    public DataRespond getComment(@PathVariable Integer comment_id) {
        return commentService.getFatherComment(comment_id);
    }

    // 获取跟帖评论内容
    @GetMapping("/v1/reply/{reply_id}")
    public DataRespond getReply(@PathVariable Integer reply_id){
        return replyService.getReplyContent(reply_id);
    }


    //新建一份试卷
    @PostMapping("/v1/test")
    private MsgRespond creatTest(@Validated @RequestBody TestReq testReq) throws ParseException {
        return testService.creatTest(testReq);
    }


    // 编辑试卷和分配试题
    @PostMapping("/v1/test/{test_id}")
    public MsgRespond insertQuestion(@PathVariable
                                     @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "test_id必须为纯数字字段")
                                     int test_id,
                                     @Validated @RequestBody QuestionReq req) {
        return testService.creatQuestion(test_id, req);
    }


    //设置试卷状态为发布
    @PutMapping("/v1/test/{test_id}")
    public MsgRespond PublishTest(@PathVariable
                                  @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "test_id必须为纯数字字段")
                                  int test_id) {
        return testService.publishTest(test_id);
    }


    //获取讲师的试卷列表
    @GetMapping("/v1/test/{teacher_id}/{lesson_id}/{page_size}/{offset}")
    public DataRespond getListByLessonIdAndTeaId(@PathVariable
                                                 @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "teacher_id必须为纯数字字段")
                                                 int teacher_id,
                                                 @PathVariable
                                                 @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "lesson_id必须为纯数字字段")
                                                 int lesson_id,
                                                 @PathVariable
                                                 @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "page_size必须为纯数字字段")
                                                 int page_size,
                                                 @PathVariable
                                                 @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "offset必须为纯数字字段")
                                                 int offset,
                                                 @RequestHeader("auth") String auth) {
        return testService.getListByLessonIdAndTeaId(lesson_id, teacher_id, page_size, offset, auth);
    }


    //获取试卷的所有试题
    @GetMapping("/v1/test/{test_id}")
    public DataRespond getAllQuestion(@PathVariable
                                      @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "test_id必须为纯数字字段")
                                      int test_id,
                                      @RequestHeader("auth") String auth) throws ParseException {
        return testService.getQuestion(test_id, auth);
    }


    //删除指定试卷
    @DeleteMapping("v1/test/{test_id}")
    public MsgRespond deleteQuestion(@PathVariable
                                     @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "test_id必须为纯数字字段")
                                     int test_id) throws ParseException {
        return testService.deleteQuestion(test_id);
    }


    //学员针对指定试卷进行作答并交卷
    @GetMapping("/v1/test/{test_id}/{student_id}")
    public MsgRespond StuSubmitQuestion(@PathVariable
                                        @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "test_id必须为纯数字字段")
                                        int test_id,
                                        @PathVariable
                                        @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "student_id必须为纯数字字段")
                                        int student_id,
                                        @RequestBody AnswerRequest answerRequest) throws ParseException {
        return testService.StuSubmitQuestion(test_id, student_id, answerRequest);
    }


    //学员查看考试结果
    @GetMapping("/v1/test/score/{test_id}/{student_id}")
    public DataRespond StuGetResult(@PathVariable
                                    @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "test_id必须为纯数字字段")
                                    int test_id,
                                    @PathVariable
                                    @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "student_id必须为纯数字字段")
                                    int student_id) {
        return testService.getResultOfTest(test_id, student_id);
    }


    //获取指定测试中学员的评估报告
    @GetMapping("/v1/test/report/{test_id}/{page_size}/{offset}")
    public DataRespond TeaGetAllScore(@PathVariable
                                      @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "test_id必须为纯数字字段")
                                      int test_id, @PathVariable
                                      @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "page_size必须为纯数字字段")
                                      int page_size,
                                      @PathVariable
                                      @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "offset必须为纯数字字段")
                                      int offset) {
        return testService.TeaGetAllScore(test_id, page_size, offset);
    }


    //获取测验的各项平均成绩列表
    @GetMapping("/v1/test/score/{lesson_id}/{page_size}/{offset}")
    public DataRespond getLessonAllScore(@PathVariable
                                         @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "lesson_id必须为纯数字字段")
                                         int lesson_id,
                                         @PathVariable
                                         @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "page_size必须为纯数字字段")
                                         int page_size,
                                         @PathVariable
                                         @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "offset必须为纯数字字段")
                                         int offset) {
        return testService.getLessonAllScore(lesson_id, page_size, offset);
    }


    //编辑试卷信息
    @PutMapping("/v1/test/info/{test_id}")
    public MsgRespond updateTest(@PathVariable
                                 @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "test_id必须为纯数字字段")
                                 int test_id,
                                 @Validated @RequestBody UpdateTestReq req) throws ParseException {
        return testService.updateTest(req, test_id);
    }


    //暂时保存讲师编写的试卷
    @PostMapping("/v1/test/cache/{test_id}")
    public MsgRespond CacheQuestion(@PathVariable
                                    @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "test_id必须为纯数字字段")
                                    int test_id,
                                    @Validated @RequestBody CacheReq req) {
        return testService.saveCache(test_id, req);
    }

    @GetMapping("/v1/test/info/{test_id}")
    public DataRespond getTestInfo(@PathVariable("test_id") Integer testId) {
        return testService.getTestInfo(testId);
    }

}
