package com.training.plan.service.Impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.training.common.entity.*;
import com.training.plan.client.DeptClient;
import com.training.plan.client.ProgressClient;
import com.training.plan.entity.request.PlanUpdateReq;
import com.training.plan.entity.request.TestReq;
import com.training.plan.entity.request.TrainingPlanReq;
import com.training.plan.entity.result.DeptInfo;
import com.training.plan.entity.result.TrainPlanInfo;
import com.training.plan.entity.table.TrainingPlanTable;
import com.training.plan.mapper.*;
import com.training.plan.reposoty.LessonCache;
import com.training.plan.reposoty.PlanCache;
import com.training.plan.service.ChapterService;
import com.training.plan.service.TrainingPlanService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 计划管理业务具体实现
 */
@Service
@AllArgsConstructor
@Transactional
public class TrainingPlanServiceImpl implements TrainingPlanService {

    private final TrainingPlanMapper trainingPlanMapper;
    private final DeptClient deptClient;
    private final PlanCache planCache;
    private final LessonMapper lessonMapper;
    private final ChapterMapper chapterMapper;
    private final LessonCache lessonCache;
    private final ProgressClient progressClient;
    private final ChapterService chapterService;



    /**
     * 创建计划的具体实现
     *
     * @param req 请求实体
     * @return 根据处理结果返回对应消息
     */
    @Override
    public MsgRespond creatTrainingPlan(TrainingPlanReq req) throws ParseException {
        //判断计划名是否已经存在
        String checkResult = checkPlanNameExit(req.getTraining_title());
        if (!checkResult.isBlank()) {
            return MsgRespond.fail(checkResult);
        }
        //判断起始时间是否早于现在，且结束时间是否早于起始时间
        SimpleDateFormat si = new SimpleDateFormat("yyyy-MM-dd");
        if (si.parse(req.getTraining_start_time()).getTime() > si.parse(req.getTraining_end_time()).getTime()
        ) {
            return MsgRespond.fail("结束时间必须在起始时间之后！");
        }
        if (si.parse(req.getTraining_start_time()).getTime() >= System.currentTimeMillis()){
            return MsgRespond.fail("起始时间不能早于今天！");
        }
        req.setTraining_state("ongoing");
        Integer Mark =trainingPlanMapper.insertTrainingPlan(req);
        return Mark>0 ? MsgRespond.success("创建成功！"):MsgRespond.fail("创建失败!");
    }
    /**
     * 获取所有计划的具体实现
     * @param page_size 请求实体
     * @param offset 请求实体
     * @return 根据处理结果返回对应消息
     */
    @Override
    public DataRespond getAllPlan(int page_size, int offset) {
        Integer count = trainingPlanMapper.getPlanCount();
        //判断计划列表是否为空
        if (count == 0) {
            return new DataFailRespond("计划列表为空！");
        }
        return new DataPagingSuccessRespond("已获取到计划列表！", count, trainingPlanMapper.getAllPlan(page_size, offset));
    }
    /**
     * 根据部门id获取计划的具体实现
     * @param page_size 请求实体
     * @param offset 请求实体
     * @return 根据处理结果返回对应消息
     */
    @Override
    public DataRespond getDeptAllPlan(int dept_id, int page_size, int offset) {
        //判断指定部门是否存在
        Integer count = trainingPlanMapper.getDeptPlanCount(dept_id);
        if (count == 0) {
            return new DataFailRespond("该部门未创建计划！");
        }
        //获取指定部门信息
        DeptInfo deptInfo = getDeptInfo(dept_id);
        //获取指定部门计划列表
        List<TrainingPlanTable> tables = trainingPlanMapper.getDeptAllPlan(dept_id,page_size,offset);
        List<TrainPlanInfo> list = new ArrayList<>();
        for(TrainingPlanTable table:tables){
            TrainPlanInfo trainPlanInfo = new TrainPlanInfo(table,deptInfo);
            list.add(trainPlanInfo);
        }
        return new DataPagingSuccessRespond("已获取到指定部门的列表！", count,list);
    }
    /**
     * 通过id获取指定计划具体实现
     * @param lesson_id 课程id
     * @param req 请求试题
     * @return 根据处理结果返回对应消息
     */
    @Override
    public MsgRespond addTest(int lesson_id, TestReq req) {
        String test_state;
        //此处跳过了视频材料存在性检验，有待补充

        //判断视频的题目数量，最多只能添加4道题目
        if (trainingPlanMapper.getTestSub(lesson_id)>=4){
            return MsgRespond.fail("题目添加失败，单个视频教材限制最多4道测试题");
        }

        //判断是多选还是单选,通过判断答案字符串的长度，如果大于1就是多选
        if (req.getTest_options_answer().length()>1) {
            test_state = "mult_select";
        } else {
            test_state = "radio";
        }

        //执行增加题目
        int insertMark = trainingPlanMapper.insertTest(req,lesson_id,test_state);
        return insertMark > 0
                ? MsgRespond.success("成功添加题目")
                : MsgRespond.fail("添加题目失败");
    }
    /**
     * 通过id获取指定计划具体实现
     * @param plan_id 请求实体
     * @return 根据处理结果返回对应消息
     */
    @Override
    public DataRespond getTrainPlanById(int plan_id) {
        //判断计划是否存在
        TrainingPlanTable trainingPlanTable = trainingPlanMapper.getTrainById(plan_id);
        if (trainingPlanTable == null){
            return new DataFailRespond("该计划未存在！");
        }
        //获取计划对应的部门信息
        DeptInfo deptInfo = getDeptInfo(trainingPlanTable.getDept_id());
        TrainPlanInfo trainPlanInfo = new TrainPlanInfo(trainingPlanTable,deptInfo);
        return new DataSuccessRespond("查询成功！",trainPlanInfo);
    }
    /**
     * 通过id修改指定计划具体实现
     * @param id 计划id
     * @param req 请求实体
     * @return 根据处理结果返回对应消息
     */
    @Override
    public MsgRespond UpdatePlan(int id, PlanUpdateReq req) throws ParseException {
        //判断是否存在该计划
        String CheckMark = checkPlanExit(id);
        if (!CheckMark.isBlank()){
            return MsgRespond.fail(CheckMark);
        }
        //使结束时间不得早于起始时间
        TrainingPlanTable planTable = trainingPlanMapper.getTrainById(id);
        SimpleDateFormat si = new SimpleDateFormat("yyyy-MM-dd");
        if(si.parse(req.getTraining_end_time()).getTime() < si.parse(planTable.getTraining_start_time()).getTime()){
            return MsgRespond.fail("结束时间不得早于起始时间");
        }
        //编辑计划
        Integer i = trainingPlanMapper.update(id, req);
        return i>0?MsgRespond.success("修改成功！"):MsgRespond.fail("修改失败");
    }
    /**
     * 更改计划状态的具体实现
     * @param id 计划id
     * @param state 请求状态
     * @return 根据处理结果返回对应消息
     */
    @Override
    public MsgRespond changeState(String state, int id) {
        //判断计划是否存在
        String CheckMark = checkPlanExit(id);
        if (!CheckMark.isBlank()){
            return MsgRespond.fail(CheckMark);
        }
        //判断计划状态是否与要修改的状态一致
        TrainingPlanTable planTable = trainingPlanMapper.getTrainById(id);
        if (Objects.equals(planTable.getTraining_state(), state)){
            return MsgRespond.fail("当前状态已是"+state);
        }
        List<String> stateRules = Arrays.asList("timeout", "end", "over", "ongoing");
        if (!stateRules.contains(state)){
            return MsgRespond.fail("支持的状态仅有四种英文形式：timeout超时、end已结束、over已完成，ongoing正在进行");
        }
        Integer i = trainingPlanMapper.changeState(state,id);
        return i>0?MsgRespond.success("修改成功！"):MsgRespond.fail("修改失败!");
    }
    /**
     * 根据id删除指定计划的具体实现
     * @param id 计划id
     * @return 根据处理结果返回对应消息
     */
    @Override
    public MsgRespond deletePlan(int id) {
        //判断计划是否存在
        TrainingPlanTable trainingPlanTable = trainingPlanMapper.getTrainById(id);
        if (trainingPlanTable == null){
            return MsgRespond.fail("未找到该计划！");
        }

        //删除计划对应的教师和学生缓存
        planCache.deleteTeacherByPlanId(id);
        planCache.deleteStudentByPlanId(id);

        //删除计划列表下的课程与其对应章节
        JSONObject res = progressClient.getLessonIdByPlanId(id, 999999, 0);
        if (Objects.equals(res.getInteger("code"), 5005)){
            return MsgRespond.fail("删除失败，原因：进度跟踪服务错误");
        }
        List<Integer> lessonIdList = res.getJSONArray("data").toJavaList(Integer.class);
        List<Integer> failList = new ArrayList<>();
        lessonIdList.forEach(lessonId -> {
            lessonCache.deleteChapter(String.valueOf(lessonId));
            Integer code = chapterService.deleteAllChapterByLessonId(lessonId).getCode();
            if (code == 5005){
                failList.add(lessonId);
            }
            lessonMapper.deleteLessonById(lessonId);
        });

        if (Objects.equals(failList.size(), lessonIdList.size())){
            return MsgRespond.fail("内部服务错误，删除失败");
        }

        //删除计划
        int i = trainingPlanMapper.DeletePlan(id);
        if(i<=0){
            return MsgRespond.fail("删除失败！");
        }





        return MsgRespond.success("删除成功！");
    }

    /**
     * 根据关键词模糊查询计划信息
     * @param keyword 关键词
     * @param page_size 读取记录
     * @param offset 偏移量
     * @return 结果列表或错误提示
     * by organwalk 2023-11-08
     */
    @Override
    public DataRespond getAllPlanByKeyword(String keyword, int page_size, int offset) {
        Integer sumMark = trainingPlanMapper.selectPlanSumByKeyword(keyword);
        if (sumMark == 0){
            return new DataFailRespond("搜索结果为空");
        }
        return new DataPagingSuccessRespond("已成功获取搜索结果", sumMark,
                trainingPlanMapper.selectAllPlanByKeyword(keyword, page_size, offset));
    }


    /**
     * 判断计划名是否存在的具体实现
     * @param title 计划名
     * @return 根据处理结果返回对应消息
     */
    private String checkPlanNameExit(String title) {
        Integer IdMark = trainingPlanMapper.selectTrainingTitleExist(title);
        if (Objects.nonNull(IdMark)) {
            return "该部门已经存在！";
        }
        return "";
    }
    /**
     * 判断部门名获取部门的具体实现
     * @param dept_id 请求实体
     * @return 根据处理结果返回对应消息
     */
    private DeptInfo getDeptInfo(int dept_id){
        JSONObject req = deptClient.getDeptInfoByDeptId(dept_id);
        JSONObject data = req.getJSONObject("data");
        int deptId = data.getInteger("id");
        String dept_name = data.getString("deptName");
        int head_id = data.getInteger("headId");
        String extra = data.containsKey("extra") ? data.getString("extra") : null;
        return new DeptInfo(deptId, dept_name, head_id, extra);
    }
    private String checkPlanExit(int id){
        TrainingPlanTable table = trainingPlanMapper.getTrainById(id);
        if (table == null){
            return "该计划不存在";
        }
        return "";
    }


}
