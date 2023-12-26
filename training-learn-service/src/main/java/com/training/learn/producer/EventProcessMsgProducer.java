package com.training.learn.producer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.training.learn.config.RabbitMQConfig;
import com.training.learn.entity.msg.*;
import com.training.learn.utils.JSONUtils;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class EventProcessMsgProducer {
    private final RabbitTemplate rabbitTemplate;
    private final JSONUtils jsonUtils;

    /**
     * 触发试卷提交处理，生产处理消息
     * @param testMsg 试卷处理消息
     * by organwalk 2023-12-25
     */
    public void triggerTestProcess(TestMsg testMsg){
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.TEST_PROCESS_EXCHANGE,
                RabbitMQConfig.TEST_PROCESS_ROUTING_KEY,
                jsonUtils.getJSONObjectByte((JSONObject) JSON.toJSON(testMsg))
        );
    }

    /**
     * 触发点赞处理，生产处理消息
     * @param likeMsg 点赞处理消息
     * by organwalk 2023-12-25
     */
    public void triggerLikeProcess(LikeMsg likeMsg){
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.LIKE_PROCESS_EXCHANGE,
                RabbitMQConfig.LIKE_PROCESS_ROUTING_KEY,
                jsonUtils.getJSONObjectByte((JSONObject) JSON.toJSON(likeMsg))
        );
    }

    /**
     * 触发点赞处理，生产点赞推送处理消息
     * @param likePushMsg 点赞推送处理消息
     */
    public void triggerLikePush(LikePushMsg likePushMsg){
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.LIKE_PUSH_EXCHANGE,
                RabbitMQConfig.LIKE_PUSH_ROUTING_KEY,
                jsonUtils.getJSONObjectByte((JSONObject) JSON.toJSON(likePushMsg))
        );
    }

    /**
     * 触发回复点赞处理，生产处理消息
     * @param replyLikeMsg 点赞处理消息
     * by organwalk 2023-12-25
     */
    public void triggerReplyLikeProcess(ReplyLikeMsg replyLikeMsg){
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.REPLY_LIKE_PROCESS_EXCHANGE,
                RabbitMQConfig.REPLY_LIKE_PROCESS_ROUTING_KEY,
                jsonUtils.getJSONObjectByte((JSONObject) JSON.toJSON(replyLikeMsg))
        );
    }

    /**
     * 触发回复点赞推送
     * @param replyLikePushMsg 推送内容
     * by organwalk 2023-12-26
     */
    public void triggerReplyLikePush(ReplyLikePushMsg replyLikePushMsg){
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.REPLY_LIKE_PUSH_EXCHANGE,
                RabbitMQConfig.REPLY_LIKE_PUSH_ROUTING_KEY,
                jsonUtils.getJSONObjectByte((JSONObject) JSON.toJSON(replyLikePushMsg))
        );
    }

    /**
     * 触发考试发布通知推送
     * @param releaseTestPushMsg 推送内容
     * by organwalk 2023-12-26
     */
    public void triggerReleaseTestPush(ReleaseTestPushMsg releaseTestPushMsg){
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.RELEASE_TEST_PUSH_EXCHANGE,
                RabbitMQConfig.RELEASE_TEST_PUSH_ROUTING_KEY,
                jsonUtils.getJSONObjectByte((JSONObject) JSON.toJSON(releaseTestPushMsg))
        );
    }

    public void triggerReplyPush(ReplyPushMsg msg){
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.REPLY_PUSH_EXCHANGE,
                RabbitMQConfig.REPLY_PUSH_ROUTING_KEY,
                jsonUtils.getJSONObjectByte((JSONObject) JSON.toJSON(msg))
        );
    }
}
