package com.training.learn.service.Impl;

import com.alibaba.fastjson.JSONArray;
import com.training.common.entity.MsgRespond;
import com.training.common.entity.req.UserInfoListReq;
import com.training.learn.client.UserClient;
import com.training.learn.mapper.CommentMapper;
import com.training.learn.mapper.LikeMapper;
import com.training.learn.mapper.ReplyMapper;
import com.training.learn.reposoty.LikeCache;
import com.training.learn.service.LikeService;
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
public class LikeServiceImpl implements LikeService {

    private final LikeMapper likeMapper;
    private final CommentMapper commentMapper;
    private final ReplyMapper replyMapper;
    private final LikeCache likeCache;
    private final UserClient userClient;


    /**
     * 对主评论点赞或取消点赞
     *
     * @param comment_id 主评论id
     * @param user_id    用户id
     * @param state      点赞状态
     * @return 根据处理结果返回对应消息
     * 2023/11/9
     */
    @Override
    public MsgRespond LikeComment(int user_id, int comment_id, int state) {
        //判断学生是否存在
        String StuMark = judgeUser(user_id);
        if (!StuMark.isBlank()) {
            return MsgRespond.fail(StuMark);
        }
        //判断评论是否存在
        String Comment_Mark = judgeCommentExit(comment_id);
        if (!Comment_Mark.isBlank()) {
            return MsgRespond.fail(Comment_Mark);
        }
        //判断是否已经点赞
        Integer OldState = likeMapper.judgeLikeOrNot(comment_id, user_id);
        //获取该评论对应的课程id
        Integer lesson_id = commentMapper.getLessonIdByCommentId(comment_id);
        String key = String.valueOf(lesson_id);
        String field = String.valueOf(comment_id);
        //获取缓存中对应评论的点赞数
        Integer sum = (Integer) likeCache.getCommentLike(key, field);
        Integer i = 0;
        if (OldState != null) {
            //如果未点赞，且进行取消点赞操作
            if (OldState == 0 && state == 0) {
                return MsgRespond.fail("未对该评论点赞");
            } else if (OldState == 1 && state == 0) {
                //如果已经点赞，且进行取消点赞操作
                //删除点赞记录
                likeMapper.DeleteByComIdAndUserId(comment_id, user_id);
                //判断缓存是否为空，非空则删除缓存
                if (sum != null) {
                    likeCache.deleteCommentLike(key, field);
                }
                return MsgRespond.success("成功取消点赞！");
            } else if (OldState == 0 && state == 1) {
                //如果有表中有记录且未进行点赞，且进行点赞操作
                //修改点赞状态为1
                i = likeMapper.updateStateToOne(comment_id, user_id);
                //判断缓存是否为空，非空则删除记录
                if (sum != null) {
                    likeCache.deleteCommentLike(key, field);
                }
            } else if (OldState == 1 && state == 1) {
                //如果已经点赞，且进行点赞操作
                return MsgRespond.fail("请勿重复点赞！");
            }
        }
        //如果没有点赞记录
        if (OldState == null) {
            //若执行取消点赞
            if (state == 0) {
                return MsgRespond.fail("未对该评论点赞");
            } else if (state == 1) {
                //执行点赞
                String time = getNowTime();
                i = likeMapper.likeComment(user_id, comment_id, 1, time);
                if (sum != null) {
                    likeCache.deleteCommentLike(key, field);
                }
            }
        }
        return i > 0 ? MsgRespond.success("点赞成功！") : MsgRespond.fail("点赞失败！");
    }


    /**
     * 对回复点赞或取消点赞
     *
     * @param reply_id 回复id
     * @param user_id  用户id
     * @param state    点赞状态
     * @return 根据处理结果返回对应消息
     * 2023/11/9
     */
    @Override
    public MsgRespond LikeReply(int user_id, int reply_id, int state) {
        //判断回复是否存在
        Integer replyMark = replyMapper.judgeReplyExitById(reply_id);
        if (replyMark == null) {
            return MsgRespond.fail("该回复不存在");
        }
        //判断学生存在性
        String StuMark = judgeUser(user_id);
        if (!StuMark.isBlank()) {
            return MsgRespond.fail(StuMark);
        }
        //获取是否拥有点赞记录
        Integer OldState = likeMapper.judgeReplyLikeOrNot(user_id, reply_id);
        //获取评论id
        Integer CommentId = replyMapper.getCommentIdById(reply_id);
        //通过评论Id获取课程id
        Integer lessonId = commentMapper.getLessonIdByCommentId(CommentId);
        String Key = String.valueOf(lessonId);
        String field = String.valueOf(reply_id);
        //获取缓存中的点赞数
        Integer sum = (Integer) likeCache.getReplyLike(Key, field);
        Integer i = 0;
        if (OldState != null) {
            if (OldState == 0 && state == 0) {
                return MsgRespond.fail("未对该回复点赞！");
            } else if (OldState == 0 && state == 1) {
                i = likeMapper.UpdateReplyStateSetOne(reply_id, user_id);
                if (sum != null) {
                    likeCache.deleteReplyLike(Key, field);
                }
            } else if (OldState == 1 && state == 0) {
                Integer j = likeMapper.UpdateReplyStateSetZero(reply_id, user_id);
                if (sum != null) {
                    likeCache.deleteReplyLike(Key, field);
                }
                return j > 0 ? MsgRespond.success("取消点赞成！") : MsgRespond.fail("取消点赞失败！");
            } else if (OldState == 1 && state == 1) {
                return MsgRespond.fail("请勿重复点赞！");
            }
        }
        if (OldState == null) {
            if (state == 0) {
                return MsgRespond.fail("未对该回复点赞！");
            } else if (state == 1) {
                String time = getNowTime();
                i = likeMapper.LikeReply(user_id, reply_id, 1, time);
                if (sum != null) {
                    likeCache.deleteReplyLike(Key, field);
                }
            }
        }
        return i > 0 ? MsgRespond.success("点赞成功！") : MsgRespond.fail("点赞失败！");
    }


    /**
     * 获取当前时间
     *
     * @return 根据处理结果返回对应消息
     * 2023/11/8
     */
    private String getNowTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        return now.format(formatter);
    }


    /**
     * 判断是否为员工
     *
     * @param student_id 学生id
     * @return 根据处理结果返回对应消息
     */
    private String judgeUser(int student_id) {
        List<Integer> uidList = new ArrayList<>();
        uidList.add(student_id);
        JSONArray uidJsonArray = userClient.getUserInfoByUidList(new UserInfoListReq(uidList));
        if (uidJsonArray.isEmpty()) {
            return "该用户不存在";
        }
        return "";
    }


    /**
     * 判断评论是否存在
     *
     * @param comment_id 评论id
     * @return 根据处理结果返回对应消息
     */
    private String judgeCommentExit(int comment_id) {
        Integer i = commentMapper.judgeCommentExit(comment_id);
        return i > 0 ? "" : "该评论不存在！";
    }


}
