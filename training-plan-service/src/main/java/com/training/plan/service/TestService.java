package com.training.plan.service;

import com.training.common.entity.DataRespond;
import com.training.common.entity.MsgRespond;
import com.training.plan.entity.request.TestReq;

public interface TestService {
    DataRespond getAllTestByRLId(int resource_lesson_id);

    DataRespond getTestById(int id);

    MsgRespond deleteById(int id);

    MsgRespond deleteAllTest(int resource_lesson_id);

    MsgRespond updateTest(TestReq req,int id);
}
