package com.push.mapper;

import com.push.entity.result.NotificationTypeResult;
import com.push.entity.table.NotificationReception;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface NotificationReceptionMapper {
    @Insert({
            "<script>",
            "INSERT INTO t_push_notification_reception (notification_id, notification_receiver_id) VALUES ",
            "<foreach collection='items' item='receiveId' separator=','>",
            "(#{notification_id}, #{receiveId})",
            "</foreach>",
            "</script>"
    })
    void batchInsertReception(@Param("items") List<Integer> receiveIdList,
                              @Param("notification_id") Integer notificationId);

    @Select("select COUNT(id) from t_push_notification_reception where notification_receiver_id = #{uid}")
    Integer countReceptionList(Integer uid);
    @Select("select id, notification_id, notification_receiver_id, is_read " +
            "from t_push_notification_reception " +
            "where notification_receiver_id = #{uid} order by is_read limit #{pageSize} offset #{offset} ")
    List<NotificationReception> selectReceptionList(@Param("uid") Integer uid,
                                                    @Param("pageSize") Integer pageSize,
                                                    @Param("offset") Integer offset);
    @Update("update t_push_notification_reception set is_read = 1 where notification_receiver_id = #{uid} and notification_id = #{notification_id}")
    void updateNotificationRead(@Param("uid") Integer uid,
                                @Param("notification_id") Integer notification_id);

    @Select("select count(n.id) " +
            "from t_push_notification as n join t_push_notification_reception AS r ON n.id = r.notification_id " +
            "WHERE r.notification_receiver_id = #{uid} AND n.notification_source_id = #{sourceId}")
    Integer countNotificationTypeResult(@Param("uid")Integer uid,
                                        @Param("sourceId")Integer sourceId);
    @Select("SELECT n.id,n.notification_type,n.notification_content,n.notification_source_id,n.notification_quote_id,n.notification_uid,n.create_datetime,r.is_read " +
            "FROM t_push_notification AS n " +
            "JOIN t_push_notification_reception AS r ON n.id = r.notification_id " +
            "WHERE r.notification_receiver_id = #{uid}" +
            "  AND n.notification_source_id = #{sourceId} order by is_read limit #{pageSize} offset #{offset}")
    List<NotificationTypeResult> selectNotificationTypeResult(@Param("uid")Integer uid,
                                                              @Param("sourceId")Integer sourceId,
                                                              @Param("pageSize")Integer pageSize,
                                                              @Param("offset")Integer offset);
}
