package com.training.learn.service.Impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.training.common.entity.DataRespond;
import com.training.common.entity.DataSuccessRespond;
import com.training.common.entity.MsgRespond;
import com.training.learn.client.ResourceClient;
import com.training.learn.entity.request.NoteReq;

import com.training.learn.entity.table.Comment;
import com.training.learn.mapper.CommentMapper;
import com.training.learn.mapper.ReplyMapper;
import com.training.learn.producer.MsgProducer;
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

/**
 * by zhaozhifeng 2023-11-10
 * by organwalk 2023-12-02
 */
@Service
@AllArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {
    private final CommentMapper commentMapper;
    private final ReplyMapper replyMapper;
    private final ResourceClient resourceClient;
    private final LikeCache likeCache;
    private final MsgProducer msgProducer;


    /**
     * 指定课程章节下发布评论
     *
     * @param request 请求实体
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

        // 生产者发送校验消息
        String msg = msgProducer.triggerCommentMsg(user_id, lesson_id, chapter_id);
        if (!Objects.equals(msg, "success")) {
            return MsgRespond.fail(msg);
        }

        //判断评论内容为普通字符串或json字符串
        Object content = jsonObject.get("content");
        String contentString;
        if (content instanceof String) {
            if (((String) content).length() > 150) {
                return MsgRespond.fail("评论字数不得超过150字");
            }
            contentString = (String) content;
        } else {
            contentString = JSON.toJSONString(content);
        }

        //插入回复
        Integer i = commentMapper.insertCommentTwo(user_id, lesson_id, chapter_id, contentString, getNowTime());
        return i > 0 ? MsgRespond.success("评论成功") : MsgRespond.fail("评论失败！");
    }


    /**
     * 删除指定主评论
     *
     * @param comment_id 评论id
     * @return 根据处理结果返回对应消息
     * 2023/11/12
     */
    @Override
    public MsgRespond DeleteComment(int comment_id) {
        Integer Id = commentMapper.judgeCommentExit(comment_id);
        if (Id == 0) {
            return MsgRespond.fail("该评论不存在！");
        }
        Integer lesson_id = commentMapper.getLessonIdByCommentId(comment_id);
        String key = String.valueOf(lesson_id);
        String field = String.valueOf(comment_id);
        Comment comment = commentMapper.getCommentById(comment_id);
        Integer i = commentMapper.deleteCommentById(comment_id);
        if (judgeNote(comment.getContent())) {
            int user_id = comment.getUserId();
            JSONObject req = JSON.parseObject(comment.getContent());
            Integer note_id = req.getInteger("note_id");
            JSONObject res = resourceClient.deleteNoteResource(user_id, note_id);
            if (Objects.equals(res.getInteger("code"), 5005)){
                return MsgRespond.fail(res.getString("msg"));
            }
        }
        Integer sum = (Integer) likeCache.getCommentLike(key, field);
        if (sum != null) {
            likeCache.deleteCommentLike(key, field);
        }
        List<Integer> replyIdList = replyMapper.getReplyIdByCommentId(comment_id);
        Integer j = replyMapper.DeleteReplyByCommentId(comment_id);
        for (Integer r : replyIdList) {
            String reply_field = String.valueOf(r);
            Integer reply_sum = (Integer) likeCache.getReplyLike(key, reply_field);
            if (reply_sum != null) {
                likeCache.deleteReplyLike(key, reply_field);
            }
        }
        return i >= 0 && j >= 0 ? MsgRespond.success("已成功删除此评论") : MsgRespond.fail("删除失败");
    }


    /**
     * 发布学习笔记
     *
     * @param user_id    用户id
     * @param lesson_id  课程id
     * @param chapter_id 章节id
     * @param noteReq    请求实体
     * @return 根据处理结果返回对应消息
     * 2023/11/12
     */
    @Override
    public MsgRespond insertNote(Integer user_id, Integer lesson_id, Integer chapter_id, NoteReq noteReq) {
        JSONObject res = resourceClient.getNoteDetail(noteReq.getResource_note_id());

        if (Objects.equals(res.getInteger("code"), 5005)) {
            return MsgRespond.fail(res.getString("msg"));
        }

        if (!Objects.equals(res.getInteger("lessonId"), lesson_id)
                || !Objects.equals(res.getInteger("chapterId"), chapter_id)
                || !Objects.equals(res.getInteger("user_id"), user_id)) {
            return MsgRespond.fail("请求信息与系统记录不一致");
        }

        Integer i = commentMapper.insertCommentTwo(user_id, lesson_id, chapter_id, JSON.toJSONString(noteReq), getNowTime());

        return i > 0 ? MsgRespond.success("已成功发布此笔记") : MsgRespond.fail("笔记发布失败");

    }

    @Override
    public DataRespond getFatherComment(Integer id) {
        return new DataSuccessRespond("已成功获取主评论内容", commentMapper.getCommentById(id));
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
     * 判断是否为笔记
     *
     * @return 根据处理结果返回对应消息
     * 2023/11/12
     */
    private boolean judgeNote(String content) {
        String regex = "^\\{\"note_id\":\\d+,\"note_title\":\"[^\"]+\",\"note_des\":\"[^\"]+\"\\}$";
        if (content.isBlank()) {
            return false;
        }
        return Pattern.matches(regex, content);
    }
}
