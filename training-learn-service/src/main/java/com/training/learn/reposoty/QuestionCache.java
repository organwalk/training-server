package com.training.learn.reposoty;


import com.training.learn.entity.request.CacheReq;
import com.training.learn.entity.result.TeaQuestionResult;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionCache {
    //保存试题
    void saveQuestion(int test_id, List<TeaQuestionResult.Question> question);
    //获取指定试题
    List<TeaQuestionResult.Question> getQuestion(int test_id);
    //删除指定试题
    void deleteQuestion(int test_id);
    //保存暂时编写的试题
    void saveCache(int test_id, CacheReq req);
    //删除暂时编写的试题
    List<CacheReq.Question> getCache(int test_id);
    //判断指定测试的缓存是否存在
    boolean judgeKeyExit(int test_id);
}
