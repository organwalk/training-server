package com.push.server;

import com.alibaba.fastjson.JSON;
import com.push.entity.PushNotification;
import com.push.service.NotificationService;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@ServerEndpoint(value = "/push/{uid}")
@Component
public class WebSocketServer {
    private static final Logger logger = LogManager.getLogger(WebSocketServer.class);

    private static final ConcurrentMap<Integer, Session> sessionMap = new ConcurrentHashMap<>();
    private static NotificationService notificationService;

    @Autowired
    public void setNotificationService(NotificationService notificationService){
        WebSocketServer.notificationService = notificationService;
    }

    @OnOpen
    public void onOpen(@PathParam("uid") Integer uid, Session session) {
        sessionMap.put(uid, session);
        logger.info("用户uid={}上线, 当前服务在线人数为：{}", uid, sessionMap.size());
    }

    @OnClose
    public void onClose(@PathParam("uid") Integer uid) {
        sessionMap.remove(uid);
        logger.info("用户uid={}下线，已移除其连接状态, 当前在线人数为：{}", uid, sessionMap.size());
    }


    @OnMessage
    public void onMessage(String message, @PathParam("uid") Integer uid) {
        logger.info("服务端收到用户uid={}的消息:{}", uid, message);

        // 解析JSON消息
        PushNotification pushNotification = JSON.parseObject(message).toJavaObject(PushNotification.class);
        // 将通知插入数据库中
        notificationService.notificationUser(pushNotification, uid);
        // 为在线用户实时推送消息
        try (ExecutorService executorService = Executors.newFixedThreadPool(10)) {
            pushNotification.getNotification_receiver_list().forEach(receiverId -> {
                Session session = sessionMap.get(receiverId);
                if (Objects.nonNull(session)) {
                    executorService.submit(() -> sendMessageToUidList(sessionMap.get(receiverId), pushNotification));
                }
            });
            executorService.shutdown();
        }

    }

    @OnError
    public void onError(Throwable error) {
        logger.error(error);
    }


    private void sendMessageToUidList(Session session, PushNotification pushNotification) {
        String message = pushNotification.getNotification_content();
        try {
            logger.info("服务端为客户端[{}]推送消息{}", session.getId(), message);
            session.getBasicRemote().sendText(message);
        } catch (Exception e) {
            logger.error("服务端消息推送失败", e);
        }
    }
}

