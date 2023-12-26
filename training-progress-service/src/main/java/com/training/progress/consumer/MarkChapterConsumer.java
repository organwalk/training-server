package com.training.progress.consumer;

import com.alibaba.fastjson.JSONObject;
import com.training.progress.config.RabbitMqConfig;
import com.training.progress.entity.request.ProgressChapterLessonReq;
import com.training.progress.mapper.ProgressChapterMapper;
import com.training.progress.mapper.ProgressLessonMapper;
import com.training.progress.utils.JSONUtils;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@AllArgsConstructor
public class MarkChapterConsumer {
    private final JSONUtils jsonUtils;
    private final ProgressLessonMapper lessonMapper;
    private final ProgressChapterMapper chapterMapper;

    @RabbitListener(queues = RabbitMqConfig.MARK_CHAPTER_QUEUE)
    public void markChapterProcessMessage(byte[] messageBody) {
        JSONObject msg = jsonUtils.getJSONObj(messageBody);
        Integer lessonId = msg.getInteger("lessonId");
        Integer chapterId = msg.getInteger("chapterId");
        Integer userId = msg.getInteger("userId");
        String nowDateTime = msg.getString("nowDateTime");

        Integer ProChapterExit = chapterMapper.judgeExitInTable(chapterId, lessonId, userId);
        if (Objects.equals(ProChapterExit, 0)) {
            ProgressChapterLessonReq progressChapterLessonReq =
                    new ProgressChapterLessonReq(lessonId, userId, chapterId, nowDateTime);
            chapterMapper.insertChapterCompletion(progressChapterLessonReq);
            lessonMapper.updateChapterSum(lessonId, userId);
        }
    }
}
