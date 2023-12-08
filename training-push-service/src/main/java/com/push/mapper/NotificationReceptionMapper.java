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
    @Select("SELECT pnr.id, pnr.notification_id, pnr.notification_receiver_id, pnr.is_read " +
            "FROM t_push_notification_reception pnr " +
            "JOIN t_push_notification pn ON pnr.notification_id = pn.id " +
            "WHERE pnr.notification_receiver_id = #{uid} " +
            "ORDER BY pnr.is_read, STR_TO_DATE(pn.create_datetime, '%Y-%m-%d %H:%i:%s') DESC " +
            "LIMIT #{pageSize} OFFSET #{offset}")
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
    @Select("SELECT n.id, n.notification_type, n.notification_content, n.notification_source_id, " +
            "n.notification_quote_id, n.notification_uid, n.create_datetime, r.is_read " +
            "FROM t_push_notification AS n " +
            "JOIN t_push_notification_reception AS r ON n.id = r.notification_id " +
            "WHERE r.notification_receiver_id = #{uid} " +
            "AND n.notification_source_id = #{sourceId} " +
            "ORDER BY r.is_read, STR_TO_DATE(n.create_datetime, '%Y-%m-%d %H:%i:%s') DESC " +
            "LIMIT #{pageSize} OFFSET #{offset}")
    List<NotificationTypeResult> selectNotificationTypeResult(@Param("uid")Integer uid,
                                                              @Param("sourceId")Integer sourceId,
                                                              @Param("pageSize")Integer pageSize,
                                                              @Param("offset")Integer offset);
}
