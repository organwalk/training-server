package com.training.learn.service;

import com.training.common.entity.DataRespond;
import com.training.common.entity.MsgRespond;
import com.training.learn.entity.request.*;


import java.text.ParseException;

public interface TestService {
    //创建测试
    MsgRespond creatTest(TestReq req) throws ParseException;
    //添加测试试题
    MsgRespond creatQuestion(int test_id,QuestionReq req);
    //发布测试
    MsgRespond publishTest(int test_id);
    //获取讲师的试卷列表
    DataRespond getListByLessonIdAndTeaId(int lesson_id,int teacher_id,int page_size,int offset,String auth);
    //获取试卷的所有试题
    DataRespond getQuestion(int test_id,String auth) throws ParseException;
    //删除指定试卷
    MsgRespond deleteQuestion(int test_id) throws ParseException;
    //学员针对指定试卷进行作答并交卷
    MsgRespond StuSubmitQuestion(int test_id, int student_id, AnswerRequest answerRequest) throws ParseException;
    //学员查看考试结果
    DataRespond getResultOfTest(int test_id,int student_id);
    //获取指定测试中学员的评估报告
    DataRespond TeaGetAllScore(int test_id,int page_size,int offset);
    //获取测验的各项平均成绩列表
    DataRespond getLessonAllScore(int lesson_id,int page_size,int offset);
    //编辑试卷信息
    MsgRespond updateTest(UpdateTestReq updateTestReq, int id) throws ParseException;
    //暂时保存讲师编写的试卷
    MsgRespond saveCache(int test_id, CacheReq req);
    // 获取试卷基本信息
    DataRespond getTestInfo(Integer id);

    DataRespond getTestPaperListByLessonIdAndTeaId(Integer lesson_id,Integer teacher_id,Integer type);

    DataRespond getIsOverTestPaperIdList(Integer studentId, Integer testId);
}
