package com.training.plan.service.Impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.training.common.entity.*;
import com.training.plan.client.DeptClient;
import com.training.plan.entity.request.TrainingPlanReq;
import com.training.plan.entity.result.DeptInfo;
import com.training.plan.entity.result.TrainPlanInfo;
import com.training.plan.entity.table.TrainingPlanTable;
import com.training.plan.service.TrainingPlanService;
import com.training.plan.mapper.TrainingPlanMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
/**
 * 计划管理业务具体实现
 */
@Service
@AllArgsConstructor
@Transactional
public class TrainingPlanServiceImpl implements TrainingPlanService {

    private final TrainingPlanMapper trainingPlanMapper;
    private final DeptClient deptClient;
    /**
     * 创建计划的具体实现
     * @param req 请求实体
     * @return 根据处理结果返回对应消息
     */
    @Override
    public MsgRespond creatTrainingPlan(TrainingPlanReq req) throws ParseException {
        String checkResult = checkPlanNameExit(req);
        if(!checkResult.isBlank()){
            return MsgRespond.fail(checkResult);
        }
        SimpleDateFormat si=new SimpleDateFormat("yyyy-MM-dd");
        //确保结束时间必须晚于起始时间并且起始时间不得早于目前的时间
        if(si.parse(req.getTraining_start_time()).getTime()<si.parse(req.getTraining_end_time()).getTime() &&
                si.parse(req.getTraining_start_time()).getTime()>=System.currentTimeMillis()){
            return MsgRespond.fail("结束时间必须在起始时间之后");
        }
        //默认state为ongoning
        req.setTraining_state("ongoing");
        trainingPlanMapper.insertTrainingPlan(req);
        return MsgRespond.success("创建成功！");
    }
    /**
     * 获取计划列表的具体实现
     * @param page_size 读取记录数
     * @param offset 从第几条读取
     * @return 根据处理结果返回对应消息
     */
    @Override
    public DataRespond getAllPlan(int page_size, int offset) {
        Integer count = trainingPlanMapper.getPlanCount();
        if(count == 0 ){
            return new DataFailRespond("计划列表为空！");
        }
        return new DataPagingSuccessRespond("已获取到计划列表！",count,trainingPlanMapper.getAllPlan(page_size,offset));
    }
    /**
     * 获取指定部门计划列表的具体实现
     * @param dept_id 部门id
     * @param page_size 读取记录数
     * @param offset 从第几条开始读取
     * @return 根据处理结果返回对应消息
     */
    @Override
    public DataRespond getDeptAllPlan(int dept_id, int page_size, int offset) {
        String result = checkDeptExit(dept_id);
        if(!result.isBlank()){
            return new DataFailRespond(result);
        }
        //查询是否存在该部门
        Integer count = trainingPlanMapper.getDeptPlanCount(dept_id);
        if(count == 0){
            return new DataFailRespond("该部门未创建计划！");
        }
        //获取指定部门的全部计划
        List<TrainingPlanTable> tables = trainingPlanMapper.getDeptAllPlan(dept_id,page_size,offset);
        //获取对应部门id的信息
        DeptInfo deptInfo = getDeptById(dept_id);
        List<TrainPlanInfo> list = new ArrayList<>();
        for (TrainingPlanTable table:tables){
            TrainPlanInfo trainPlanInfo = new TrainPlanInfo(table,deptInfo);
            list.add(trainPlanInfo);
        }
        return new DataPagingSuccessRespond("已获取到指定部门的列表！",count,list);
    }
    /**
     * 根据计划id获取计划的具体实现
     * @param id 计划id
     * @return 根据处理结果返回对应消息
     */
    @Override
    public DataRespond getTrainPlanById(int id) {
        String checkResult = checkPlanExitByID(id);
        if (!checkResult.isBlank()){
            return new DataFailRespond(checkResult);
        }
        //获取几乎详情
        TrainingPlanTable table = trainingPlanMapper.getTrainById(id);
        //获取计划对应部门详情
        DeptInfo deptInfo = getDeptById(table.getDept_id());
        TrainPlanInfo trainPlanInfo = new TrainPlanInfo(table,deptInfo);
        return new DataSuccessRespond("成功获取计划详细",trainPlanInfo);
    }


    /**
     * 判断计划名是否存在的具体实现
     * @param req 请求实体
     * @return 根据处理结果返回对应消息
     */
    private String checkPlanNameExit(TrainingPlanReq req){
        Integer IdMark = trainingPlanMapper.selectTrainingTitleExist(req.getTraining_title());
        if(Objects.nonNull(IdMark)){
            return "该计划已经存在！";
        }
        return "";
    }

    /**
     * 判断部门是否存在
     * @param dept_id 部门id
     * @return 根据处理结果返回对应消息
     */
    private String checkDeptExit(int dept_id){
        JSONObject req = deptClient.getDeptInfoByDeptId(dept_id);
        if (Objects.equals(req.get("code"),5005)){
            return "该部门不存在！";
        }
        return "";
    }
    /**
     * 通过计划id判断计划是否存在
     * @param id 计划id
     * @return 根据处理结果返回对应消息
     */
    private String checkPlanExitByID(int id){
        TrainingPlanTable table = trainingPlanMapper.getTrainById(id);
        if (table == null){
            return "该计划不存在！";
        }
        return "";
    }
    /**
     * 根据部门id获取部门
     * @param dept_id 部门id
     * @return 根据处理结果返回对应消息
     */
    private DeptInfo getDeptById(int dept_id){
        JSONObject req = deptClient.getDeptInfoByDeptId(dept_id);
        JSONObject data = req.getJSONObject("data");
        int deptId = data.getInteger("id");
        String dept_name = data.getString("deptName");
        int head_id = data.getInteger("headId");
        //判断data中是否有key为extra，有则赋值到extra中
        String extra = data.containsKey("extra")?data.getString("extra"):null;
        DeptInfo deptInfo = new DeptInfo(deptId,dept_name,head_id,extra);
        return deptInfo;
    }


}
