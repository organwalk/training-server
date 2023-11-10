package com.training.learn.controller;

import com.training.common.entity.DataRespond;
import com.training.common.entity.MsgRespond;
import com.training.learn.service.CommentService;
import com.training.learn.service.LikeService;
import com.training.learn.service.ReplyService;
import com.training.learn.service.TrainingService;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/learn")
@AllArgsConstructor
public class LearnController {

    private final TrainingService trainingService;
    private final CommentService commentService;
    private final ReplyService replyService;
    private final LikeService likeService;

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
                                      int offset){
        return trainingService.getPlanListByStuId(student_id,page_size,offset);
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
                                                  int offset){
        return trainingService.getLessonByPIdAndStuId(plan_id,student_id,page_size,offset);
    }


    //获取指定课程下的章节列表
    @GetMapping("/v1/plan/lesson/chapter/{lesson_id}/{student_id}")
    public DataRespond getChapterByLessIdAndStuId(@PathVariable
                                                      @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "student_id必须为纯数字字段")
                                                      int student_id,
                                                  @PathVariable
                                                  @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "plan_id必须为纯数字字段")
                                                  int lesson_id){
        return trainingService.getChapterByStuIdAndLessId(student_id,lesson_id);
    }


    //指定课程发布评论
    @PostMapping("/v1/lesson/comment")
    public MsgRespond insertLessonContent(@RequestBody String request){
        return commentService.insertCommentOne(request);
    }



    //指定课程章节下发布评论
    @PostMapping("/v1/lesson/chaper/comment")
    public MsgRespond insertChapterContent(@RequestBody String request){
        return commentService.insertCommentTwo(request);
    }



    //指定评论发表回复
    @PostMapping("/v1/lesson/reply")
    public MsgRespond replyComment(@RequestBody String request){
        return replyService.ReplyComment(request);
    }



    //指定跟帖回复进行再跟帖
    @PostMapping("/v1/lesson/reply/thread")
    public MsgRespond ThreadReply(@RequestBody String request){
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
                                           @Min(value = 0,message = "lesson_state只能为0或1")
                                           @Max(value = 1,message = "lesson_state只能为0或1")
                                           int state
                                           ){
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
                                      int page_size,@PathVariable
                                      @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "offset必须为纯数字字段")
                                      int offset){
        return replyService.getCommentList(lesson_id,user_id,page_size,offset);
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
                                        int page_size,@PathVariable
                                        @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "offset必须为纯数字字段")
                                        int offset){
        return replyService.getReplyList(comment_id,user_id,page_size,offset);
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
                                                                int page_size,@PathVariable
                                                                @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "offset必须为纯数字字段")
                                                                int offset){
        return replyService.getCommentListByLessonIdAndChapterId(lesson_id,chapter_id,user_id,page_size,offset);
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
                                             @Min(value = 0,message = "lesson_state只能为0或1")
                                             @Max(value = 1,message = "lesson_state只能为0或1")
                                         int state){
        return likeService.LikeReply(user_id,reply_id,state);
    }

}
