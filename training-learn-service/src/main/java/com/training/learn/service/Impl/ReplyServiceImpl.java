package com.training.learn.service.Impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.training.common.entity.DataFailRespond;
import com.training.common.entity.DataPagingSuccessRespond;
import com.training.common.entity.DataRespond;
import com.training.common.entity.MsgRespond;
import com.training.learn.client.DeptClient;
import com.training.learn.client.PlanClient;
import com.training.learn.client.UserClient;
import com.training.learn.entity.result.CommentList;
import com.training.learn.entity.result.ReplyList;
import com.training.learn.entity.table.Comment;
import com.training.learn.entity.table.Reply;
import com.training.learn.mapper.CommentMapper;
import com.training.learn.mapper.LikeMapper;
import com.training.learn.mapper.ReplyMapper;
import com.training.learn.reposoty.LikeCache;
import com.training.learn.service.ReplyService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
@Transactional
public class ReplyServiceImpl implements ReplyService {

    private final CommentMapper commentMapper;
    private final ReplyMapper replyMapper;
    private final LikeMapper likeMapper;
    private final PlanClient planClient;
    private final UserClient userClient;
    private final DeptClient deptClient;
    private final LikeCache likeCache;



    /**
     *  指定评论发表回复
     * @param  request 请求实体
     * @return 根据处理结果返回对应消息
     * 11/8
     */
    @Override
    public MsgRespond ReplyComment(String request) {
        JSONObject req = JSON.parseObject(request);
        Integer user_id = req.getInteger("user_id");
        Integer comment_id = req.getInteger("comment_id");
        String StuMark = judgeUser(user_id);
        if (!StuMark.isBlank()){
            return MsgRespond.fail(StuMark);
        }
        String CommentMark = judgeCommentExit(comment_id);
        if (!CommentMark.isBlank()){
            return MsgRespond.fail(CommentMark);
        }
        String content = req.getString("content");
        if(content.length()>150){
            return MsgRespond.fail("评论不得超过150字");
        }
        String time = getNowTime();
        Integer i = replyMapper.replyComment(user_id,comment_id,content,time);
        return i>0?MsgRespond.success("已成功为此评论发表回复"):MsgRespond.fail("评论失败！");
    }



    /**
     *   指定跟帖回复进行再跟帖
     * @param  request 请求实体
     * @return 根据处理结果返回对应消息
     * 11/9
     */
    @Override
    public MsgRespond ThreadReply(String request) {
        JSONObject req = JSON.parseObject(request);
        Integer user_id = req.getInteger("user_id");
        Integer commend_id = req.getInteger("comment_id");
        JSONObject content_req =  req.getJSONObject("content");
        Integer reply_user_id = content_req.getInteger("reply_user_id");
        String StuMark =judgeUser(user_id);
        String Reply_StuMark = judgeUser(reply_user_id);
        if (!StuMark.isBlank() || !Reply_StuMark.isBlank()){
            return MsgRespond.fail(StuMark);
        }
        String ReplyMark = judgeReplyExit(commend_id,user_id);
        if (!ReplyMark.isBlank()){
            return MsgRespond.fail(ReplyMark);
        }
        String time = getNowTime();
        Object content = req.get("content");
        String contentString = JSON.toJSONString(content);
        Integer i = replyMapper.replyComment(user_id,commend_id,contentString,time);
        return i>0?MsgRespond.success("已成功回复此跟帖"):MsgRespond.fail("回复失败！");
    }




    /**
     *   获取课程的评论列表
     * @param  lesson_id 课程id
     * @param user_id 用户id
     * @return 根据处理结果返回对应消息
     * 11/9
     */
    @Override
    public DataRespond getCommentList(int lesson_id, int user_id, int page_size, int offset) {
        String LessonMark = judgeLessonExit(lesson_id);
        if (!LessonMark.isBlank()){
            return new DataFailRespond("该课程不存在！");
        }
        String StuMark = judgeUserExit(user_id);
        if (!StuMark.isBlank()){
            return new DataFailRespond("该用户不存在！");
        }
        List<Comment> commentList = commentMapper.getCommentList(lesson_id);
        List<CommentList> commentLists = new ArrayList<>();
        String Key = String.valueOf(lesson_id);
        for(Comment comment :commentList){
            String comment_real_name = getUserName(comment.getUserId());
            String comment_dept_name = getDeptName(comment.getUserId());
            String Comment_field = String.valueOf(comment.getId());
            Integer comment_like_sum = (Integer) likeCache.getCommentLike(Key,Comment_field);
            if (comment_like_sum ==null){
                comment_like_sum = likeMapper.getCommentLikeCount(comment.getId());
                likeCache.saveCommentLike(Key,Comment_field,comment_like_sum);
             }
            Integer comment_state = likeMapper.getStateByCommentId(comment.getId());
            if (comment_state==null){
                comment_state=0;
            }
            List<Reply> replies = replyMapper.getReplyListByCommentId(comment.getId());
            List<ReplyList> replyLists = new ArrayList<>();
            for (Reply reply:replies){
                String Reply_field = String.valueOf(reply.getId());
                Integer reply_sum = (Integer) likeCache.getReplyLike(Key, Reply_field);
                if (reply_sum==null){
                     reply_sum = likeMapper.getReplyLikeCountByComIDAndReplyId(comment.getId(),reply.getId());
                     likeCache.saveReplyLike(Key,Reply_field,reply_sum);
                }
                Integer reply_state = likeMapper.getStateByCommentIdAndReplyId(comment.getId(),reply.getId(),user_id);
                if (reply_state==null){
                    reply_state=0;
                }
                String reply_real_name =getUserName(reply.getUser_id());
                String reply_dept_name = getDeptName(reply.getUser_id());
                ReplyList replyList = new ReplyList(reply.getId(),reply.getUser_id(),reply_real_name,reply_dept_name,reply.getContent(),reply.getCreate_datetime(),reply_sum,reply_state);
                replyLists.add(replyList);
            }
            CommentList commentList1 = new CommentList(comment.getId(),comment.getUserId(),comment_real_name,comment_dept_name,comment.getContent(),comment.getCreateDatetime(),comment_like_sum,comment_state,new CommentList.Reply_Obj(replies.size(),replyLists));
            commentLists.add(commentList1);
        }
        int endIndx = Math.min(offset + page_size,commentLists.size());
        List<CommentList> result = commentLists.subList(offset,endIndx);
        return new DataPagingSuccessRespond("已成功获取该课程评论列表",commentLists.size(),result);
    }




    /**
     * 获取主评论的跟帖回复列表
     * @param  comment_id 评论id
     * @param user_id 用户id
     * @return 根据处理结果返回对应消息
     * 11/10
     */
    @Override
    public DataRespond getReplyList(int comment_id, int user_id, int page_size, int offset) {
        String StuMark = judgeUserExit(user_id);
        if(!StuMark.isBlank()){
            return new DataFailRespond(StuMark);
        }
        String CommentMark = judgeCommentExit(comment_id);
        if(!CommentMark.isBlank()){
            return new DataFailRespond(CommentMark);
        }
        List<Reply> replies = replyMapper.getReplyListByCommentId(comment_id);
        Integer lesson_id = commentMapper.getLessonIdByCommentId(comment_id);
        String ReplyKey = String.valueOf(lesson_id);
        List<ReplyList> replyLists = new ArrayList<>();
        for (Reply reply:replies){
            String Reply_field = String.valueOf(reply.getId());
            Integer reply_sum = (Integer) likeCache.getReplyLike(ReplyKey, Reply_field);
            if (reply_sum==null){
                reply_sum = likeMapper.getReplyLikeCountByComIDAndReplyId(comment_id,reply.getId());
                likeCache.saveReplyLike(ReplyKey,Reply_field,reply_sum);
            }
            Integer reply_state = likeMapper.getStateByCommentIdAndReplyId(comment_id,reply.getId(),user_id);
            if (reply_state==null){
                reply_state=0;
            }
            String reply_real_name =getUserName(reply.getUser_id());
            String reply_dept_name = getDeptName(reply.getUser_id());
            ReplyList replyList = new ReplyList(reply.getId(),reply.getUser_id(),reply_real_name,reply_dept_name,reply.getContent(),reply.getCreate_datetime(),reply_sum,reply_state);
            replyLists.add(replyList);
        }
        int endIndx = Math.min(offset + page_size,replyLists.size());
        List<ReplyList> result = replyLists.subList(offset,endIndx);
        return new DataPagingSuccessRespond("已成功获取该评论的跟帖回复列表",replyLists.size(),result);
    }




    /**
     * 获取课程章节的评论列表
     * @param  lesson_id 课程id
     * @param chapter_id 章节id
     * @param user_id 用户id
     * @return 根据处理结果返回对应消息
     * 11/10
     */
    @Override
    public DataRespond getCommentListByLessonIdAndChapterId(int lesson_id, int chapter_id, int user_id, int page_size, int offset) {
        String StuMark = judgeUserExit(user_id);
        if (!StuMark.isBlank()){
            return new DataFailRespond(StuMark);
        }
        String LessonMark = judgeLessonExit(lesson_id);
        if (!LessonMark.isBlank()){
            return new DataFailRespond(LessonMark);
        }
        String ChapterMark = judgeChapterExit(lesson_id,chapter_id);
        if (!ChapterMark.isBlank()){
            return new DataFailRespond(ChapterMark);
        }
        List<Comment> commentList = commentMapper.getCommentByLessonIdAndChapterId(lesson_id,chapter_id);
        List<CommentList> commentLists = new ArrayList<>();
        String Key = String.valueOf(lesson_id);
        for(Comment comment:commentList){
            String comment_real_name = getUserName(comment.getUserId());
            String comment_dept_name = getDeptName(comment.getUserId());
            String Comment_field = String.valueOf(comment.getId());
            Integer comment_like_sum = (Integer) likeCache.getCommentLike(Key,Comment_field);
            if (comment_like_sum ==null){
                comment_like_sum = likeMapper.getCommentLikeCount(comment.getId());
                likeCache.saveCommentLike(Key,Comment_field,comment_like_sum);
            }
            Integer comment_state = likeMapper.getStateByCommentId(comment.getId());
            if (comment_state==null){
                comment_state=0;
            }
            List<Reply> replies = replyMapper.getReplyListByCommentId(comment.getId());
            List<ReplyList> replyLists = new ArrayList<>();
            for (Reply reply:replies){
                String Reply_field = String.valueOf(reply.getId());
                Integer reply_sum = (Integer) likeCache.getReplyLike(Key, Reply_field);
                if (reply_sum==null){
                    reply_sum = likeMapper.getReplyLikeCountByComIDAndReplyId(comment.getId(),reply.getId());
                    likeCache.saveReplyLike(Key,Reply_field,reply_sum);
                }
                Integer reply_state = likeMapper.getStateByCommentIdAndReplyId(comment.getId(),reply.getId(),user_id);
                if (reply_state==null){
                    reply_state=0;
                }
                String reply_real_name =getUserName(reply.getUser_id());
                String reply_dept_name = getDeptName(reply.getUser_id());
                ReplyList replyList = new ReplyList(reply.getId(),reply.getUser_id(),reply_real_name,reply_dept_name,reply.getContent(),reply.getCreate_datetime(),reply_sum,reply_state);
                replyLists.add(replyList);
            }
            CommentList commentList1 = new CommentList(comment.getId(),comment.getUserId(),comment_real_name,comment_dept_name,comment.getContent(),comment.getCreateDatetime(),comment_like_sum,comment_state,new CommentList.Reply_Obj(replies.size(),replyLists));
            commentLists.add(commentList1);
        }
        int endIndx = Math.min(offset + page_size,commentLists.size());
        List<CommentList> result = commentLists.subList(offset,endIndx);
        return new DataPagingSuccessRespond("已成功获取该课程评论列表",commentLists.size(),result);
    }


    /**
     *  判断是否为员工
     * @param  student_id 学生id
     * @return 根据处理结果返回对应消息
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
     *  判断评论是否存在
     * @param  comment_id 评论id
     * @return 根据处理结果返回对应消息
     */
    private String judgeCommentExit(int comment_id){
        Integer i = commentMapper.judgeCommentExit(comment_id);
        return i>0?"":"该评论不存在！";
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
     *  判断回复是否存在
     * @param comment_id 主评论id
     * @param user_id 用户id
     * @return 根据处理结果返回对应消息
     * 2023/11/8
     */
    private String judgeReplyExit(int comment_id,int user_id){
        Integer i = replyMapper.judgeReplyExit(comment_id,user_id);
        if (i==0){
            return "该评论不存在！";
        }
        return "";
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
     *  判断用户是否存在
     * @param  user_id 用户id
     * @return 根据处理结果返回对应消息
     * 2023/11/9
     */
    private String judgeUserExit(int user_id){
        JSONObject req = userClient.getUserAccountByUid(user_id);
        if (Objects.equals(req.get("code"),5005)){
            return "该用户不存在！";
        }
        return "";
    }


    /**
     *  根据id获取用户真实姓名
     * @param  user_id 用户id
     * @return 根据处理结果返回对应消息
     * 2023/11/9
     */
    private String getUserName(int user_id){
        JSONObject req = userClient.getUserAccountByUid(user_id);
        JSONObject date = req.getJSONObject("data");
        return date.getString("realName");
    }



    /**
     *  根据课程id获取部门名称
     * @param  user_id 用户id
     * @return 根据处理结果返回对应消息
     * 2023/11/9
     */
    private String getDeptName(int user_id){
       Integer dept_id = deptClient.getDeptIdByUserId(user_id);
       JSONObject req = deptClient.getDeptInfo(dept_id);
       JSONObject data = req.getJSONObject("data");
       return data.getString("deptName");
    }



    /**
     *  判断章节是否存在
     * @param  lesson_id 课程id
     * @param  chapter_id 章节id
     * @return 根据处理结果返回对应消息
     * 2023/11/10
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






}
