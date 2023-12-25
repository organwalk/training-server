package com.training.learn.entity.msg;

import com.training.learn.entity.request.AnswerRequest;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TestMsg {
    private String msgId;
    private String nowDateTime;
    private Integer testId;
    private Integer studentId;
    private Integer lessonId;
    private AnswerRequest answerRequest;
}
