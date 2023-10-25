package com.training.plan.service.Impl;

import com.training.common.entity.*;
import com.training.plan.entity.request.TestReq;
import com.training.plan.entity.request.TrainingPlanReq;
import com.training.plan.service.TrainingPlanService;
import com.training.plan.mapper.TrainingPlanMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Objects;

/**
 * 计划管理业务具体实现
 */
@Service
@AllArgsConstructor
@Transactional
public class TrainingPlanServiceImpl implements TrainingPlanService {

    private final TrainingPlanMapper trainingPlanMapper;


    /**
     * 创建计划的具体实现
     *
     * @param req 请求实体
     * @return 根据处理结果返回对应消息
     */
    @Override
    public MsgRespond creatTrainingPlan(TrainingPlanReq req) throws ParseException {
        String checkResult = checkPlanNameExit(req);
        if (!checkResult.isBlank()) {
            return MsgRespond.fail(checkResult);
        }
        SimpleDateFormat si = new SimpleDateFormat("yyyy-MM-dd");
        if (si.parse(req.getTraining_start_time()).getTime() < si.parse(req.getTraining_end_time()).getTime() &&
                si.parse(req.getTraining_start_time()).getTime() >= System.currentTimeMillis()) {
            return MsgRespond.fail("结束时间必须在起始时间之后");
        }
        req.setTraining_state("ongoing");
        trainingPlanMapper.insertTrainingPlan(req);
        return MsgRespond.success("创建成功！");
    }

    @Override
    public DataRespond getAllPlan(int page_size, int offset) {
        Integer count = trainingPlanMapper.getPlanCount();
        if (count == 0) {
            return new DataFailRespond("计划列表为空！");
        }
        return new DataPagingSuccessRespond("已获取到计划列表！", count, trainingPlanMapper.getAllPlan(page_size, offset));
    }

    @Override
    public DataRespond getDeptAllPlan(int dept_id, int page_size, int offset) {
        Integer count = trainingPlanMapper.getDeptPlanCount(dept_id);
        if (count == 0) {
            return new DataFailRespond("该部门未创建计划！");
        }
        return new DataPagingSuccessRespond("已获取到指定部门的列表！", count, trainingPlanMapper.getDeptAllPlan(dept_id, page_size, offset));
    }

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
                : MsgRespond.fail("创建账号失败，请重试");
    }


    /**
     * 判断计划名是否存在的具体实现
     *
     * @param req 请求实体
     * @return 根据处理结果返回对应消息
     */
    private String checkPlanNameExit(TrainingPlanReq req) {
        Integer IdMark = trainingPlanMapper.selectTrainingTitleExist(req.getTraining_title());
        if (Objects.nonNull(IdMark)) {
            return "该部门已经存在！";
        }
        return "";
    }


}
