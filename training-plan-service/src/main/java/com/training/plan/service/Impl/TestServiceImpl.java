package com.training.plan.service.Impl;

import com.training.common.entity.DataFailRespond;
import com.training.common.entity.DataRespond;
import com.training.common.entity.DataSuccessRespond;
import com.training.common.entity.MsgRespond;
import com.training.plan.entity.request.TestReq;
import com.training.plan.entity.table.TestTable;
import com.training.plan.mapper.TestMapper;
import com.training.plan.service.TestService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
@Transactional
public class TestServiceImpl implements TestService {
    private final TestMapper testMapper;
    /**
     * 获取指定视频教程的所有测试
     * @param resource_lesson_id 视频教材id
     * @return 根据处理结果返回对应消息
     */
    @Override
    public DataRespond getAllTestByRLId(int resource_lesson_id) {
        //判断测试是否拥有
        Integer Mark = testMapper.getCountByRLId(resource_lesson_id);
        if (Objects.equals(Mark,0)){
            return new DataFailRespond("该视频教程下没有测试");
        }
        List<TestTable> testTables = testMapper.getAllTestByRLId(resource_lesson_id);
        return new DataSuccessRespond("已成功获取视频教材测试题列表",testTables);
    }
    /**
     * 获取指定测试
     * @param id 试题id
     * @return 根据处理结果返回对应消息
     */
    @Override
    public DataRespond getTestById(int id) {
        TestTable table = testMapper.getTestById(id);
        return table!=null?new DataSuccessRespond("已成功获取该视频教材测试题详情",table):new DataFailRespond("未找到该试题！");
    }
    /**
     * 删除指定测试
     * @param id 试题id
     * @return 根据处理结果返回对应消息
     */
    @Override
    public MsgRespond deleteById(int id) {
        TestTable table = testMapper.getTestById(id);
        if (table==null){
            return MsgRespond.fail("该试题不存在！");
        }
        Integer i = testMapper.deleteById(id);
        return i>0?MsgRespond.success("已成功删除此视频教材测试题"):MsgRespond.fail("删除失败");
    }
    /**
     * 删除指定视频教程下的测试
     * @param resource_lesson_id 试题id
     * @return 根据处理结果返回对应消息
     */
    @Override
    public MsgRespond deleteAllTest(int resource_lesson_id) {
        Integer Mark = testMapper.judgeExit(resource_lesson_id);
        if (Objects.equals(Mark,0)){
            return MsgRespond.fail("该视频教程下不存在测试");
        }
        Integer i = testMapper.deleteByRLId(resource_lesson_id);
        return i>0?MsgRespond.success("已成功删除此视频教材下的所有测试题"):MsgRespond.fail("删除失败！");
    }

    @Override
    public MsgRespond updateTest(TestReq req, int id) {
        TestTable Mark = testMapper.getTestById(id);
        if (Mark==null){
            return MsgRespond.fail("该测试不存在");
        }
        String test_state;
        if (req.getTest_options_answer().length()>1) {
            test_state = "mult_select";
        } else {
            test_state = "radio";
        }
        Integer i = testMapper.updateTest(req,test_state,id);
        return i>0?MsgRespond.success("已成功编辑此测试题"):MsgRespond.fail("编辑失败！");
    }


}
