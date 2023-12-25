package com.training.learn.service.Impl;

import com.alibaba.fastjson.JSONArray;
import com.training.common.entity.MsgRespond;
import com.training.common.entity.req.UserInfoListReq;
import com.training.learn.client.UserClient;
import com.training.learn.entity.msg.LikeMsg;
import com.training.learn.entity.table.Comment;
import com.training.learn.mapper.CommentMapper;
import com.training.learn.mapper.LikeMapper;
import com.training.learn.mapper.ReplyMapper;
import com.training.learn.producer.EventProcessMsgProducer;
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
import java.util.UUID;

/**
 * by zhaozhifeng 2023-11-10
 * by organwalk 2023-12-09
 */
@Service
@AllArgsConstructor
@Transactional
public class LikeServiceImpl implements LikeService {

    private final LikeMapper likeMapper;
    private final CommentMapper commentMapper;
    private final ReplyMapper replyMapper;
    private final LikeCache likeCache;
    private final UserClient userClient;
    private final EventProcessMsgProducer eventProcessMsgProducer;


    /**
     * 对主评论点赞或取消点赞
     *
     * @param comment_id 主评论id
     * @param user_id    用户id
     * @param state      点赞状态
     * @return 根据处理结果返回对应消息
     * by organwalk 2023-12-25
     */
    @Override
    public MsgRespond LikeComment(int user_id, int comment_id, int state) {
        //判断学生是否存在
        String StuMark = judgeUser(user_id);
        if (!StuMark.isBlank()) {
            return MsgRespond.fail(StuMark);
        }

        Integer lessonId;
        Integer commentUser;

        //判断评论是否存在
        String cache = likeCache.getCommentLessonIdCache(comment_id);
        if (cache.isBlank()) {
            Comment comment = commentMapper.getCommentById(comment_id);
            if (Objects.isNull(comment)) {
                return MsgRespond.fail("该评论不存在");
            }

            lessonId = comment.getLessonId();
            commentUser = comment.getUserId();
            likeCache.cacheCommentLessonId(comment_id, lessonId, commentUser);
        } else {
            lessonId = Integer.valueOf(cache.split("-")[0]);
            commentUser = Integer.valueOf(cache.split("-")[1]);
        }

        // 触发点赞事件处理，生产点赞处理消息
        eventProcessMsgProducer.triggerLikeProcess(
                new LikeMsg(
                        String.valueOf(UUID.randomUUID()),
                        user_id,
                        comment_id,
                        lessonId,
                        commentUser,
                        state,
                        getNowTime()
                )
        );

        return MsgRespond.success("已进行此操作");
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
                return j > 0 ? MsgRespond.success("取消点赞成功！") : MsgRespond.fail("取消点赞失败！");
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


}
