package com.training.learn.service.Impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.training.common.entity.MsgRespond;
import com.training.learn.client.PlanClient;
import com.training.learn.client.UserClient;
import com.training.learn.mapper.CommentMapper;
import com.training.learn.service.CommentService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;


@Service
@AllArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {
    private final CommentMapper commentMapper;
    private final PlanClient planClient;
    private final UserClient userClient;



    /**
     *  指定课程发布评论
     * @param  request 请求实体
     * @return 根据处理结果返回对应消息
     * 2023/11/7
     */
    @Override
    public MsgRespond insertCommentOne(String request) {
        //判断学生与课程的存在性
        JSONObject jsonObject = JSON.parseObject(request);
        int user_id = jsonObject.getInteger("user_id");
        int lesson_id = jsonObject.getInteger("lesson_id");
        String StuMark = judgeUser(user_id);
        if (!StuMark.isBlank()){
            return MsgRespond.fail(StuMark);
        }
        String lessonMark = judgeLessonExit(lesson_id);
        if(!lessonMark.isBlank()){
            return MsgRespond.fail(lessonMark);
        }
        String time = getNowTime();
        Object content = jsonObject.get("content");
        String contentString;
        if (content instanceof String){
            contentString = (String)content;
        }else {
           contentString=JSON.toJSONString(content);
        }
        if (contentString.length()>150){
            return MsgRespond.fail("评论字数不得超过150字");
        }
        Integer i = commentMapper.insertCommentOne(user_id,lesson_id,contentString,time);
        return i>0?MsgRespond.success("当前用户已成功评论此课程"):MsgRespond.fail("评论失败！");
    }



    /**
     *  指定课程章节下发布评论
     * @param  request 请求实体
     * @return 根据处理结果返回对应消息
     * 2023/11/7
     */
    @Override
    public MsgRespond insertCommentTwo(String request) {
      JSONObject jsonObject = JSON.parseObject(request);
        int user_id = jsonObject.getInteger("user_id");
        int lesson_id = jsonObject.getInteger("lesson_id");
        int chapter_id = jsonObject.getInteger("chapter_id");
        String StuMark = judgeUser(user_id);
        if (!StuMark.isBlank()){
            return MsgRespond.fail(StuMark);
        }
        String lessonMark = judgeLessonExit(lesson_id);
        if (!lessonMark.isBlank()){
            return MsgRespond.fail(lessonMark);
        }
        String chapterMark = judgeChapterExit(lesson_id,chapter_id);
        if (!chapterMark.isBlank()){
            return MsgRespond.fail(chapterMark);
        }
        String time = getNowTime();
        Object content = jsonObject.get("content");
        String contentString;
        if (content instanceof String){
           contentString = (String)content;
        }else {
           contentString=JSON.toJSONString(content);
        }
        if (contentString.length()>150){
            return MsgRespond.fail("评论字数不得超过150字");
        }
        Integer i = commentMapper.insertCommentTwo(user_id,lesson_id,chapter_id,contentString,time);
        return i>0?MsgRespond.success("当前用户已成功评论此课程"):MsgRespond.fail("评论失败！");
    }


    /**
     *  判断课程是否存在
     * @param  lesson_id 课程id
     * @return 根据处理结果返回对应消息
     * 2023/11/7
     */
    private String judgeLessonExit(int lesson_id){
        JSONObject req = planClient.getLessonInfo(lesson_id);
        if (Objects.equals(req.get("code"),5005)){
            return "该课程不存在！";
        }
        return "";
    }


    /**
     *  判断是否为员工
     * @param  student_id 学生id
     * @return 根据处理结果返回对应消息
     * 2023/11/7
     */
    private String judgeUser(int student_id){
        List<Integer> headIdList = commentMapper.getAllHeadId();
        String result = "";
        for (Integer i:headIdList){
            if (Objects.equals(student_id,i)){
                result = "该用户不是员工！";
                break;
            }
        }
        return result;
    }


    /**
     *  判断章节是否存在
     * @param  lesson_id 课程id
     * @param  chapter_id 章节id
     * @return 根据处理结果返回对应消息
     * 2023/11/7
     */
    private String judgeChapterExit(int lesson_id,int chapter_id){
        List<Integer> chapterList = commentMapper.judgeChapterExit(lesson_id);
        String result="该章节不存在";
        for (Integer i:chapterList){
            if (Objects.equals(chapter_id,i)){
                result="";
            }
        }
        return result;
    }



    /**
     *  获取当前时间
     * @return 根据处理结果返回对应消息
     * 2023/11/8
     */
    private String getNowTime(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        return now.format(formatter);
    }







}
