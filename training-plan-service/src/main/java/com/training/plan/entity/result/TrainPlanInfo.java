package com.training.plan.entity.result;

import com.training.plan.entity.table.TrainingPlanTable;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
@Data
@AllArgsConstructor
public class TrainPlanInfo {

    private TrainingPlanTable table;
    private DeptInfo deptInfo;
}
