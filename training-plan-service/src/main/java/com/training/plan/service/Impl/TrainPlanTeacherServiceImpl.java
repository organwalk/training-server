package com.training.plan.service.Impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.training.common.entity.DataFailRespond;
import com.training.common.entity.DataPagingSuccessRespond;
import com.training.common.entity.DataRespond;
import com.training.common.entity.MsgRespond;
import com.training.common.entity.req.UserInfoListReq;
import com.training.plan.client.UserClient;
import com.training.plan.entity.respond.TeacherInfo;
import com.training.plan.entity.result.User;
import com.training.plan.entity.table.TrainingPlanTable;
import com.training.plan.mapper.TrainPlanTeacherMapper;
import com.training.plan.mapper.TrainingPlanMapper;
import com.training.plan.reposoty.PlanCache;
import com.training.plan.service.TrainPlanTeacherService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
/**
 * 计划教师管理业务具体实现
 */
@Service
@AllArgsConstructor
@Transactional
public class TrainPlanTeacherServiceImpl implements TrainPlanTeacherService {
    private final TrainPlanTeacherMapper trainPlanTeacherMapper;
    private final UserClient userClient;
    private final TrainingPlanMapper PlanMapper;
    private final PlanCache planCache;
    /**
     * 创建计划的具体实现
     * @param training_teacher_id 教师id
     * @param train_plan_id 计划id
     * @return 根据处理结果返回对应消息
     */
    @Override
    public MsgRespond insertTrainPlanTeacher(int train_plan_id,int training_teacher_id) {
        //判断教师是否已经在该计划内
        String CheckResult = judgeTeaExit(training_teacher_id,train_plan_id);
        if (!CheckResult.isBlank()){
            return MsgRespond.fail(CheckResult);
        }
        trainPlanTeacherMapper.insertTrainPlanTeacher(train_plan_id,training_teacher_id);
        return MsgRespond.success("添加教师成功！");
    }
    /**
     * 获取指定计划下教师列表具体实现
     * @param plan_id 部门ID
     * @return 返回教师列表
     */
    @Override
    public DataRespond getAllTeaByPlanId(int plan_id,int page_size,int offset) {
        //判断部门是否存在
        String checkResult = judgePlanExit(plan_id);
        if (!checkResult.isBlank()){
            return new DataFailRespond(checkResult);
        }
        //获取教师数量
        Integer sumMark = trainPlanTeacherMapper.getCountOfTea(plan_id);
        //判断redis是否拥有有则从redis获取
        String key = String.valueOf(plan_id+page_size+offset);
        if(planCache.getTeaList(key)!=null){
            String result = (String) planCache.getTeaList(key);
            List<TeacherInfo> info = JSON.parseArray(result, TeacherInfo.class);
            return  new DataPagingSuccessRespond("查询成功！",sumMark,info);
        }
        //获取教师详情
        List<Integer> teacherIdList = trainPlanTeacherMapper.getAllTeacherId(plan_id,page_size,offset);
        JSONArray TeaList = userClient.getUserInfoByUidList(new UserInfoListReq(teacherIdList));
        List<User> userList = JSONArray.parseArray(TeaList.toJSONString(),User.class);
        List<Integer> IdList = trainPlanTeacherMapper.getAllTeaId(plan_id);
        List<TeacherInfo> list = new ArrayList<>();
        for(int i =0;i<userList.size();i++){
            TeacherInfo teacherInfo = new TeacherInfo(IdList.get(i),teacherIdList.get(i),userList.get(i));
            list.add(teacherInfo);
        }

        planCache.saveTea(key,list);
        return new DataPagingSuccessRespond("查询到全部教师信息",sumMark,list);
    }
    /**
     * 获取指定计划下教师列表具体实现
     * @param t_id 教师数据ID
     * @return 返回教师列表
     */
    @Override
    public MsgRespond deleteTea(int t_id) {
        //判断该教师是否在计划内
        Integer ExitMark = trainPlanTeacherMapper.ExitJudge(t_id);
        if (Objects.equals(ExitMark,0)){
            return MsgRespond.fail("改教师未在该计划内");
        }
        //删除教师
        Integer i = trainPlanTeacherMapper.deleteByTId(t_id);
        if (i<=0){
            return MsgRespond.fail("删除失败!");
        }
        //删除缓存
        Map<Object, Object> teaList = planCache.getTeaAll();
        for (Object key:teaList.keySet()){
            String StrKey = key.toString();
            String[] parts = StrKey.split("-");
            String value = parts[0];
            if (value.equals(String.valueOf(t_id))){
                planCache.DeleteTea(key);
            }
        }
        return MsgRespond.success("删除成功！");
    }

    /**
     * 判断教师是否存在以及是否是学生的具体实现
     * @param training_teacher_id 教师id
     * @return 根据处理结果返回对应消息
     */
    private String  judgeTeaExit(int training_teacher_id,int id){
        Integer IdMark = trainPlanTeacherMapper.CheckTeaInForm(training_teacher_id,id);
        if(IdMark != 0){
            return "该教师已经存在！";
        }
        JSONObject resObject = userClient.getUserAccountByUid(training_teacher_id);
        if(Objects.equals(resObject.get("code"),5005)){
            return "该教师不存在，无法添加！";
        }
        if (!Objects.equals(resObject.getJSONObject("data").get("authId"),2)){
            return "该用户不是教师，无法添加！";
        }
        return "";
    }
    /**
     * 判断计划是否存在的具体实现
     * @param plan_id 计划id
     * @return 根据处理结果返回对应消息
     */
    private String judgePlanExit(int plan_id){
        TrainingPlanTable table = PlanMapper.getTrainById(plan_id);
        if (table == null){
            return "该计划不存在！";
        }
        return "";
    }
}
