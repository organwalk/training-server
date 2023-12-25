package com.push.consumer;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.push.config.RabbitMQConfig;
import com.push.entity.PushNotification;
import com.push.server.WebSocketServer;
import com.push.utils.JSONUtils;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class Consumer {
    private final JSONUtils jsonUtils;
    private final WebSocketServer webSocketServer;
    @RabbitListener(queues = RabbitMQConfig.LIKE_PUSH_QUEUE)
    public void likePushProcess(byte[] messageBody){
        // 从消息体中初始化变量
        JSONObject msg = jsonUtils.getJSONObj(messageBody);
        webSocketServer.pushNotification(getNotificationBody(msg), msg.getInteger("uid"));
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
