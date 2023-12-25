package com.training.learn.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.training.learn.client.ProgressClient;
import com.training.learn.config.RabbitMQConfig;
import com.training.learn.entity.request.AnswerRequest;
import com.training.learn.entity.table.TypeTable;
import com.training.learn.mapper.*;
import com.training.learn.reposoty.TestCache;
import com.training.learn.utils.JSONUtils;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@AllArgsConstructor
public class TestConsumer {
    private final QuestionMapper questionMapper;
    private final ScoreMapper scoreMapper;
    private final AnswerMapper answerMapper;
    private final TypeMapper typeMapper;
    private final ProgressClient progressClient;
    private final TestCache testCache;
    private final JSONUtils jsonUtils;

    /**
     * 监听考试结果处理队列，并针对队列中的请求进行处理
     * @param messageBody 消息内容，包含了考试提交请求的内容
     * by organwalk 2023-12-25
     */
    @RabbitListener(queues = RabbitMQConfig.TEST_PROCESS_QUEUE)
    public void testProcessMessage(byte[] messageBody){
        JSONObject msg = jsonUtils.getJSONObj(messageBody);
        String nowDateTime = msg.getString("nowDateTime");
        Integer testId = msg.getInteger("testId");
        Integer studentId = msg.getInteger("studentId");
        Integer lessonId = msg.getInteger("lessonId");
        AnswerRequest answerRequest = msg.getObject("answerRequest", AnswerRequest.class);

        String validMsg = validStudent(testId, lessonId, studentId);
        if (validMsg.isBlank()){
            processScore(testId, studentId, answerRequest, nowDateTime);
        }
    }


    /**
     * 验证学员是否在本次考试中
     * @param testId 试卷ID
     * @param lessonId 课程ID
     * @param studentId 学员ID
     * @return 当校验通过时，即学员在本次考试中，将返回空值，否则返回none
     * by organwalk 2023-12-25
     */
    private String validStudent(Integer testId, Integer lessonId, Integer studentId){
        List<Integer> studentIdList = testCache.getTestStudentCache(testId);
        if (studentIdList.isEmpty()){
            JSONObject res = progressClient.getStudentIdList(lessonId).join();
            if (Objects.equals(res.getInteger("code"), 2002)){
                studentIdList = res.getObject("data", new TypeReference<List<Integer>>(){});
                testCache.cacheTestStudent(testId, studentIdList);
            }
        }
        return studentIdList.contains(studentId)
                ? "" : "none";
    }


    /**
     * 批改学生分数
     * @param testId 试卷ID
     * @param studentId 学员ID
     * @param answerRequest 包含答案的实体
     * @param nowDateTime 试卷提交时间
     * by organwalk 2023-12-25
     */
    private void processScore(Integer testId, Integer studentId, AnswerRequest answerRequest, String nowDateTime){
        // 只批改没有分数的学生试卷
        Integer ScoreMark = scoreMapper.judgeExitByTestIdAndStuId(testId, studentId);
        if (ScoreMark == null) {
            // 初始化分数、类别分数及权重
            double must_score = 0.0;
            double importance_score = 0.0;
            double normal_score = 0.0;
            Integer must_type = 0;
            Integer importance_type = 0;
            Integer normal_type = 0;
            double must_weight = 0.0;
            double importance_weight = 0.0;
            double normal_weight = 0.0;

            List<TypeTable> typeList = typeMapper.getTypeList();
            for (TypeTable item : typeList) {
                switch (item.getId()) {
                    case 1 -> {
                        must_type = item.getScore();
                        must_weight = item.getWeight();
                    }
                    case 2 -> {
                        importance_type = item.getScore();
                        importance_weight = item.getWeight();
                    }
                    default -> {
                        normal_type = item.getScore();
                        normal_weight = item.getWeight();
                    }
                }
            }

            String answer_result = JSON.toJSONString(answerRequest.getAnswers());
            if (answerRequest.getAnswers().size() == 1 && Objects.isNull(answerRequest.getAnswers().get(0))) {
                answerMapper.insertAnswer(answer_result, studentId, testId, nowDateTime);
                scoreMapper.insertScore(0, 0, 0, 0, studentId, testId);
            }

            for (AnswerRequest.Answer answer : answerRequest.getAnswers()) {
                String true_answer = questionMapper.getAnswerById(answer.getQ_id());
                Integer importanceId = questionMapper.getImportanceIdById(answer.getQ_id());
                if (Objects.equals(true_answer, answer.getAnswer())) {
                    if (importanceId == 1) {
                        must_score += must_type * must_weight;
                    } else if (importanceId == 2) {
                        importance_score += importance_type * importance_weight;
                    } else if (importanceId == 3) {
                        normal_score += normal_type * normal_weight;
                    }
                }
            }

            //获取综合得分
            double result_all_score = must_score + importance_score + normal_score;
            //插入学生答案
            answerMapper.insertAnswer(answer_result, studentId, testId, nowDateTime);
            //插入学生得分
            scoreMapper.insertScore(result_all_score, must_score, importance_score, normal_score, studentId, testId);
        }
    }


}
