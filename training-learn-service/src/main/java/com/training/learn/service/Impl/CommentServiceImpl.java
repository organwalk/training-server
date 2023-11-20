package com.training.learn.service.Impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.training.common.entity.MsgRespond;
import com.training.learn.client.PlanClient;
import com.training.learn.client.ResourceClient;
import com.training.learn.entity.request.NoteReq;

import com.training.learn.entity.table.Comment;
import com.training.learn.mapper.CommentMapper;
import com.training.learn.mapper.ReplyMapper;
import com.training.learn.reposoty.LikeCache;
import com.training.learn.service.CommentService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;


@Service
@AllArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {
    private final CommentMapper commentMapper;
    private final ReplyMapper replyMapper;
    private final PlanClient planClient;
    private final ResourceClient resourceClient;
    private final LikeCache likeCache;





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
        //获取当前时间
        String time = getNowTime();
        //判断评论内容为普通字符串还是json字符串
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
        //插入评论
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
        //获取请求中的用户id，课程id，章节id
        int user_id = jsonObject.getInteger("user_id");
        int lesson_id = jsonObject.getInteger("lesson_id");
        int chapter_id = jsonObject.getInteger("chapter_id");
        //判断学生是否存在
        String StuMark = judgeUser(user_id);
        if (!StuMark.isBlank()){
            return MsgRespond.fail(StuMark);
        }
        //判断课程是否存在
        String lessonMark = judgeLessonExit(lesson_id);
        if (!lessonMark.isBlank()){
            return MsgRespond.fail(lessonMark);
        }
        //判断章节是否存在
        String chapterMark = judgeChapterExit(lesson_id,chapter_id);
        if (!chapterMark.isBlank()){
            return MsgRespond.fail(chapterMark);
        }
        //获取当前时间
        String time = getNowTime();
        //判断评论内容为普通字符串或json字符串
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
        //插入回复
        Integer i = commentMapper.insertCommentTwo(user_id,lesson_id,chapter_id,contentString,time);
        return i>0?MsgRespond.success("当前用户已成功评论此课程"):MsgRespond.fail("评论失败！");
    }





    /**
     *  删除指定主评论
     * @param  comment_id 评论id
     * @return 根据处理结果返回对应消息
     * 2023/11/12
     */
    @Override
    public MsgRespond DeleteComment(int comment_id) {
        Integer Id = commentMapper.judgeCommentExit(comment_id);
        if (Id == 0){
            return MsgRespond.fail("该评论不存在！");
        }
        Integer lesson_id = commentMapper.getLessonIdByCommentId(comment_id);
        String key = String.valueOf(lesson_id);
        String field = String.valueOf(comment_id);
        Comment comment = commentMapper.getCommentById(comment_id);
        Integer i = commentMapper.deleteCommentById(comment_id);
        if (judgeNote(comment.getContent())){
            int user_id = comment.getUserId();
            JSONObject req = JSON.parseObject(comment.getContent());
            Integer note_id = req.getInteger("note_id");
            resourceClient.deleteNoteResource(user_id,note_id);
        }
        Integer sum = (Integer) likeCache.getCommentLike(key,field);
        if(sum != null){
            likeCache.deleteCommentLike(key,field);
        }
        List<Integer> replyIdList = replyMapper.getReplyIdByCommentId(comment_id);
        Integer j = replyMapper.DeleteReplyByCommentId(comment_id);
        for(Integer r: replyIdList){
            String reply_field = String.valueOf(r);
            Integer reply_sum = (Integer) likeCache.getReplyLike(key,reply_field);
            if (reply_sum!=null){
                likeCache.deleteReplyLike(key,reply_field);
            }
        }
        return i>=0&&j>=0?MsgRespond.success("已成功删除此评论"):MsgRespond.fail("删除失败");
    }




    /**
     *  发布学习笔记
     * @param user_id 用户id
     * @param  lesson_id 课程id
     * @param chapter_id  章节id
     * @param  noteReq 请求实体
     * @return 根据处理结果返回对应消息
     * 2023/11/12
     */
    @Override
    public MsgRespond insertNote(Integer user_id,Integer lesson_id,Integer chapter_id,NoteReq noteReq) {
        String StuMark = judgeUser(user_id);
        if (!StuMark.isBlank()){
            return MsgRespond.fail(StuMark);
        }
        String LessonMark = judgeLessonExit(lesson_id);
        if (!LessonMark.isBlank()){
            return MsgRespond.fail(LessonMark);
        }
        String ChapterInLesson = judgeChapterExit(lesson_id,chapter_id);
        if (!ChapterInLesson.isBlank()){
            return MsgRespond.fail(ChapterInLesson);
        }
        Integer NoteExit = commentMapper.getNoteExit(noteReq.getResource_note_id());
        if (NoteExit == null){
            return MsgRespond.fail("笔记不存在");
        }
        String content = JSON.toJSONString(noteReq);
        String time =getNowTime();
        Integer i = commentMapper.insertCommentTwo(user_id,lesson_id,chapter_id,content,time);
        return i>0?MsgRespond.success("已成功发布此笔记"):MsgRespond.fail("笔记发布失败");

    }


    /**
     *  判断课程是否存在
     * @param  lesson_id 课程id
     * @return 根据处理结果返回对应消息
     * 2023/11/7
     */
    private String judgeLessonExit(int lesson_id){
        //调用培训管理服务接口判断课程是否存在
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
                break;
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



    /**
     *  判断是否为笔记
     * @return 根据处理结果返回对应消息
     * 2023/11/12
     */
    private boolean judgeNote(String content){
        String regex = "^\\{\"note_id\":\\d+,\"note_title\":\"[^\"]+\",\"note_des\":\"[^\"]+\"\\}$";
        if (content.isBlank()){
            return false;
        }
        return Pattern.matches(regex, content);
    }








}
