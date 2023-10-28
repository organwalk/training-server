package com.training.plan.entity.result;

import com.training.plan.entity.respond.TeacherInfo;
import com.training.plan.entity.table.TrainingPlanStudentTable;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlanStuInfo {
    private TeacherInfo teacherInfo;
    private DeptInfo deptInfo;
}
