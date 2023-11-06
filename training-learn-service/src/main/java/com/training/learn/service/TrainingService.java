package com.training.learn.service;

import com.training.common.entity.DataRespond;

public interface TrainingService {

    DataRespond getPlanListByStuId(int student_id,int page_size,int offset);

    DataRespond getLessonByPIdAndStuId(int plan_id,int student_id,int page_size,int offset);

}
