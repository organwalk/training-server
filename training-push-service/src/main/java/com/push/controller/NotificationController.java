package com.push.controller;

import com.push.service.NotificationService;
import com.training.common.entity.DataRespond;
import com.training.common.entity.MsgRespond;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/push")
public class NotificationController {
    private final NotificationService notificationService;
    @GetMapping("/v1/notification/{uid}/{pageSize}/{offset}")
    public DataRespond getNotificationList(@PathVariable("uid") Integer uid,
                                           @PathVariable("pageSize") Integer pageSize,
                                           @PathVariable("offset") Integer offset){
        return notificationService.getNotificationList(uid, pageSize, offset);
    }
    @PutMapping("/v1/notification/{uid}/{notification_id}")
    public MsgRespond readNotification(@PathVariable Integer uid,
                                       @PathVariable Integer notification_id){
        return notificationService.readNotification(uid, notification_id);
    }
    @GetMapping("/v1/notification/type/{uid}/{source_id}/{pageSize}/{offset}")
    public DataRespond getTypeNotificationList(@PathVariable Integer uid,
                                              @PathVariable Integer source_id,
                                              @PathVariable Integer pageSize,
                                              @PathVariable Integer offset){
        return notificationService.getTypeNotificationList(uid, source_id, pageSize, offset);
    }

}
