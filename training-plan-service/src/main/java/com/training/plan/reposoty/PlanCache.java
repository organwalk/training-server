package com.training.plan.reposoty;

import com.alibaba.fastjson.JSONArray;
import com.training.plan.entity.respond.StudentInfo;
import com.training.plan.entity.respond.TeacherInfo;
import com.training.plan.entity.result.TrainPlanInfo;
import com.training.plan.entity.table.TrainingPlanTable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface PlanCache {
    void saveTea(String key, List<TeacherInfo> infoList);

    void saveStu(String key, List<StudentInfo> infoList);

    Object getStuList(String key);

    Object getTeaList(String key);

    Map<Object, Object> getStuAll();

    Map<Object, Object> getTeaAll();

    void DeleteStu(Object key);

    void DeleteTea(Object key);

    void deleteStudentByPlanId(int planId);

    void deleteTeacherByPlanId(int planId);
}
