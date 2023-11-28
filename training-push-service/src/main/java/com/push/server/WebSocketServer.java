package com.push.server;

import com.alibaba.fastjson.JSON;
import com.push.entity.PushMessage;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@ServerEndpoint(value = "/push/{uid}")
@Component
public class WebSocketServer {
    private static final Logger logger = LogManager.getLogger(WebSocketServer.class);

    private static final ConcurrentMap<String, Session> sessionMap = new ConcurrentHashMap<>();

    /**
     * 连接建立成功时缓存连接状态
     */
    @OnOpen
    public void onOpen(@PathParam("uid") String uid, Session session) {
        sessionMap.put(uid, session);
        logger.info("用户uid={}上线, 当前服务在线人数为：{}", uid, sessionMap.size());
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(@PathParam("uid") String uid) {
        sessionMap.remove(uid);
        logger.info("用户uid={}下线，已移除其连接状态, 当前在线人数为：{}", uid, sessionMap.size());
    }


    @OnMessage
    public void onMessage(String message, @PathParam("uid") String uid) {
        logger.info("服务端收到用户uid={}的消息:{}", uid, message);
        PushMessage pushMessage = JSON.parseObject(message).toJavaObject(PushMessage.class);
        try (ExecutorService executorService = Executors.newFixedThreadPool(10)) {
            pushMessage.getTo_uid_list().forEach(to_uid -> {
                Session session = sessionMap.get(to_uid);
                if (Objects.nonNull(session)) {
                    executorService.submit(() -> sendMessageToUidList(sessionMap.get(to_uid), pushMessage));
                }
            });
            executorService.shutdown();
        }
    }

    @OnError
    public void onError(Throwable error) {
        logger.error("发生错误");
        error.printStackTrace();
    }

    /**
     * 服务端发送消息给客户端
     */
    private void sendMessageToUidList(Session session, PushMessage pushMessage) {
        String message = pushMessage.getMessage();
        try {
            logger.info("服务端为客户端[{}]推送消息{}", session.getId(), message);
            session.getBasicRemote().sendText(message);
        } catch (Exception e) {
            logger.error("服务端消息推送失败", e);
        }
    }

    /**
     * 服务端发送消息给所有客户端
     */
    private void sendAllMessage(String message) {
        sessionMap.values().forEach(session -> {
            try {
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}

