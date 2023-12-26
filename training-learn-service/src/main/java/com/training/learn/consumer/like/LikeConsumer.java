package com.training.learn.consumer.like;

import com.alibaba.fastjson.JSONObject;
import com.training.learn.config.RabbitMQConfig;
import com.training.learn.entity.msg.LikePushMsg;
import com.training.learn.mapper.LikeMapper;
import com.training.learn.producer.EventProcessMsgProducer;
import com.training.learn.reposoty.LikeCache;
import com.training.learn.utils.JSONUtils;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component
@AllArgsConstructor
public class LikeConsumer {
    private final LikeMapper likeMapper;
    private final LikeCache likeCache;
    private final JSONUtils jsonUtils;
    private final EventProcessMsgProducer eventProcessMsgProducer;
    private final LikeState likeState;

    @RabbitListener(queues = RabbitMQConfig.LIKE_PROCESS_QUEUE)
    public void likeProcessMessage(byte[] messageBody) {
        // 从消息体中初始化变量
        JSONObject msg = jsonUtils.getJSONObj(messageBody);
        Integer commentId = msg.getInteger("commentId");
        Integer userId = msg.getInteger("userId");
        Integer lessonId = msg.getInteger("lessonId");
        Integer commentUser = msg.getInteger("commentUser");
        Integer state = msg.getInteger("state");
        String nowDateTime = msg.getString("nowDateTime");

        //获取缓存中对应评论的点赞数
        Integer sum = (Integer) likeCache.getCommentLike(String.valueOf(lessonId), String.valueOf(commentId));

        // 获取点赞状态
        Integer OldState = likeMapper.judgeLikeOrNot(commentId, userId);
        StateEnum nowState = likeState.getLikeState(OldState, state);
        switch (nowState) {
            case LIKED -> likeMapper.likeComment(userId, commentId, 1, nowDateTime);
            case NOT_LIKED -> likeMapper.DeleteByComIdAndUserId(commentId, userId);
            case TO_LIKED -> likeMapper.updateStateToOne(commentId, userId);
        }

        // 点赞完成时，触发生产点赞通知推送消息
        if (nowState == StateEnum.LIKED || nowState == StateEnum.TO_LIKED){
            eventProcessMsgProducer.triggerLikePush(
                    new LikePushMsg(String.valueOf(UUID.randomUUID()), userId, commentId, List.of(commentUser))
            );
        }

        if ((nowState == StateEnum.LIKED || nowState == StateEnum.NOT_LIKED || nowState == StateEnum.TO_LIKED)
                && Objects.nonNull(sum)) {
            likeCache.deleteCommentLike(String.valueOf(lessonId), String.valueOf(commentId));
        }
    }
}
