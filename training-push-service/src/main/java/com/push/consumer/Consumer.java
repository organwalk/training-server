package com.push.consumer;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.push.client.PlanClient;
import com.push.config.RabbitMQConfig;
import com.push.entity.PushNotification;
import com.push.server.WebSocketServer;
import com.push.utils.JSONUtils;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@AllArgsConstructor
public class Consumer {
    private final JSONUtils jsonUtils;
    private final WebSocketServer webSocketServer;
    private final PlanClient planClient;

    @RabbitListener(queues = RabbitMQConfig.LIKE_PUSH_QUEUE)
    public void likePushProcess(byte[] messageBody){
        JSONObject msg = jsonUtils.getJSONObj(messageBody);
        webSocketServer.pushNotification(getNotificationBody(msg), msg.getInteger("uid"));
    }

    @RabbitListener(queues = RabbitMQConfig.REPLY_LIKE_PUSH_QUEUE)
    public void replyLikePushProcess(byte[] messageBody){
        JSONObject msg = jsonUtils.getJSONObj(messageBody);
        webSocketServer.pushNotification(getNotificationBody(msg), msg.getInteger("uid"));
    }

    @RabbitListener(queues = RabbitMQConfig.RELEASE_TEST_PUSH_QUEUE)
    public void releaseTestPushProcess(byte[] messageBody){
        JSONObject msg = jsonUtils.getJSONObj(messageBody);
        JSONObject content = new JSONObject();
        content.put("text", "您有一次测试待参加");
        content.put("lessonName", msg.getJSONObject("content").getString("lessonName"));
        msg.put("content", content);
        webSocketServer.pushNotification(getNotificationBody(msg), msg.getInteger("uid"));
    }

    @RabbitListener(queues = RabbitMQConfig.REPLY_PUSH_QUEUE)
    public void replyPushProcess(byte[] messageBody){
        JSONObject msg = jsonUtils.getJSONObj(messageBody);
        JSONObject lessonRes = planClient.getLessonInfo(msg.getJSONObject("content").getInteger("lessonId"));
        if (Objects.equals(lessonRes.getInteger("code"), 2002)){
            JSONObject chapterRes = planClient.getChapterDetail(msg.getJSONObject("content").getInteger("chapterId"));
            if (Objects.equals(chapterRes.getInteger("code"), 2002)){
                JSONObject content = new JSONObject();
                content.put("text", "您收到了回复");
                content.put("lessonName", lessonRes.getJSONObject("data").getString("lesson_name"));
                content.put("chapterName", chapterRes.getJSONObject("data").getString("chapterName"));
                content.put("comment", msg.getJSONObject("content").getString("commentText"));
                msg.put("content", content.toJSONString());
                webSocketServer.pushNotification(getNotificationBody(msg), msg.getInteger("uid"));
            }
        }
    }

    private PushNotification getNotificationBody(JSONObject msg){
        PushNotification pushNotification = new PushNotification();
        pushNotification.setAccess_token("");
        pushNotification.setNotification_type("user");
        pushNotification.setNotification_content(msg.getString("content"));
        pushNotification.setNotification_source_type(msg.getString("sourceType"));
        pushNotification.setNotification_quote_id(msg.getInteger("quoteId"));
        pushNotification.setNotification_receiver_list(msg.getObject("receiverIdList", new TypeReference<List<Integer>>() {}));
        return pushNotification;
    }


}
