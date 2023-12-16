package com.push.service;

import com.push.entity.PushNotification;
import com.training.common.entity.DataRespond;
import com.training.common.entity.MsgRespond;

/**
 * 定义推送服务持久化业务
 * by organwalk 2023-11-29
 */
public interface NotificationService {
    void notificationUser(PushNotification pushNotification, Integer uid);
    DataRespond getNotificationList(Integer uid, Integer pageSize, Integer offset);
    MsgRespond readNotification(Integer uid, Integer notificationId);
    DataRespond getTypeNotificationList(Integer uid, Integer sourceId, Integer pageSize, Integer offset);
}
