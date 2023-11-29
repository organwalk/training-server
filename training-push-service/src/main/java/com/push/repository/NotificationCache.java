package com.push.repository;

import com.push.entity.result.NotificationDetail;

import java.util.List;

public interface NotificationCache {
    void saveNotification(Integer uid, List<NotificationDetail> list);
    List<NotificationDetail> getNotification(Integer uid);
    void deleteNotification(Integer uid);

}
