package com.training.plan.service.Impl;

import com.alibaba.fastjson.JSONObject;
import com.training.common.entity.*;
import com.training.plan.client.ProgressClient;
import com.training.plan.client.UserClient;
import com.training.plan.entity.request.LessonReq;
import com.training.plan.entity.request.LessonUpdate;
import com.training.plan.entity.result.User;
import com.training.plan.entity.table.LessonTable;
import com.training.plan.mapper.ChapterMapper;
import com.training.plan.mapper.LessonMapper;
import com.training.plan.mapper.TrainPlanStudentMapper;
import com.training.plan.reposoty.LessonCache;
import com.training.plan.service.LessonService;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.List;
import java.util.Objects;

/**
 * by zhaozhifeng 2023-10-31
 */
@Service
@AllArgsConstructor
@Transactional
public class LessonServiceImpl implements LessonService {
    private final LessonMapper lessonMapper;
    private final UserClient userClient;
    private final ChapterMapper chapterMapper;
    private final TrainPlanStudentMapper trainPlanStudentMapper;
    private final ProgressClient progressClient;
    private final LessonCache lessonCache;


    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public MsgRespond insertLesson(LessonReq req,int plan_id) {
        //判断是否存在该课程
        String resultMark = CheckExit(req.getLesson_name());
        if (!resultMark.isBlank()){
            return MsgRespond.fail(resultMark);
        }
        //获取教师信息并判断是否为教师
        User user = getUser(req.getTeacher_id());
        if (Objects.isNull(user)){
            return MsgRespond.fail("用户服务异常，无法正常编辑账号信息");
        }
        if (!Objects.equals(user.getAuth_id(),2)){
            return MsgRespond.fail("该用户不是教师，请重新选择");
        }
        req.setLesson_state(0);
        LessonTable lessonTable = new LessonTable(null, req.getLesson_name(), req.getLesson_des(), req.getTeacher_id(), req.getLesson_state(), null);

        Integer result = lessonMapper.insertLesson(lessonTable);
        if (result == 0){
            return MsgRespond.fail("内部服务错误添加课程失败");
        }

        JSONObject j = progressClient.insertInProPlan(plan_id,req.getTeacher_id(),lessonTable.getId());

        return !Objects.equals(j.get("code"),5005) ? MsgRespond.success("添加课程成功！"):MsgRespond.fail(j.getString("msg"));
    }
    /**
     * 根据教师id获取教师的所有课程的具体实现
     * @param teacher_id 教师id
     * @param page_size 请求实体
     * @param offset 请求实体
     * @return 根据处理结果返回对应消息
     */
    @Override
    public DataRespond getTeaAllLess(int teacher_id,int page_size,int offset) {
        //判断教师身份
        String ExitMark = UserExit(teacher_id);
        if (!ExitMark.isBlank()){
            return new DataFailRespond("该用户不是教师");
        }
        //获取教师课程总数
        Integer count = lessonMapper.getTeaLessCount(teacher_id);
        //获取课程列表
        List<LessonTable> list = lessonMapper.getTeaAllLess(teacher_id,page_size,offset);
        return new DataPagingSuccessRespond("已成功获取该教师的课程列表",count,list);
    }
    /**
     * 根据课程id获取课程信息的具体实现
     * @param id 课程id
     * @return 根据处理结果返回对应消息
     */
    @Override
    public DataRespond getLessInfoById(int id) {
        LessonTable lessonTable = lessonMapper.getLessInfoById(id);
        return lessonTable!= null?new DataSuccessRespond("已成功返回此课程详细信息",lessonTable):new DataFailRespond("该课程不存在");
    }
    /**
     * 根编辑指定教师和课程的课程详细信息的具体实现
     * @param id 课程id
     * @param teacher_id 指定教师id
     * @param req 请求实体
     * @return 根据处理结果返回对应消息
     */
    @Override
    public MsgRespond updateLesson(LessonUpdate req, int id, int teacher_id) {
        Integer i = lessonMapper.updateLesson(req,id,teacher_id);
        return i>0?MsgRespond.success("已成功编辑此课程"):MsgRespond.fail("编辑失败");
    }
    /**
     *  删除指定教师的指定课程
     * @param id 课程id
     * @return 根据处理结果返回对应消息
     */
    @Override
    public MsgRespond DeleteLessonById(int id) {
        //判断课程是否存在
        LessonTable lessonTable = lessonMapper.getLessInfoById(id);
        if (lessonTable ==null){
            return MsgRespond.fail("该课程不存在！");
        }
        //删除指定课程
        Integer i = lessonMapper.deleteLessonById(id);
        if (i == 0) {
            return MsgRespond.fail("内部服务错误，删除失败！");
        }
        // 如果存在章节，则将章节与缓存删除
        Integer chapterMark = chapterMapper.existChapter(id);
        if (Objects.nonNull(chapterMark) && chapterMark == 1){
            Integer j = chapterMapper.deleteAllChapterByLessonId(id);
            if (j == 0 ){
                return MsgRespond.fail("内部服务错误，删除失败！");
            }
            lessonCache.deleteChapter(String.valueOf(id));
        }
        return MsgRespond.success("已成功删除此课程");



    }
    /**
     *  删除指定教师的所有课程
     * @param teacher_id 课程id
     * @return 根据处理结果返回对应消息
     */
    @Override
    public MsgRespond DeleteLessonByTId(int teacher_id) {
        //判断课程表中是否包含此id
        Integer Mark = lessonMapper.getCountByTID(teacher_id);
        if (Objects.equals(Mark,0)){
            return MsgRespond.fail("该教师未拥有教程！");
        }
        //删除指定教师id的所有课程
        Integer i = lessonMapper.deleteLessonByTId(teacher_id);
        //删除对应章节
        List<Integer> lessIdList = lessonMapper.getTeaLessIdList(teacher_id);
        for(Integer z:lessIdList){
            chapterMapper.deleteAllChapterByLessonId(z);
        }
        return i>0?MsgRespond.success("已成功删除所有课程"):MsgRespond.fail("删除失败");
    }
    /**
     *  设定课程状态为发布
     * @param id 课程id
     * @return 根据处理结果返回对应消息
     */
    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public MsgRespond updateState(int id) {
        //判断课程状态是否已经发布
        Integer state = lessonMapper.getState(id);
        if (!Objects.equals(state,0)){
            return MsgRespond.fail("该课程已经发布");
        }
        //将状态改为发布
        Integer i = lessonMapper.updateState(1,id);
        if(i>0){
            Integer planId = trainPlanStudentMapper.getPlanIdByLessonId(id);
            Integer over_chapter = 0;
            Integer chapterSum = chapterMapper.getCountByLId(id);
            List<Integer> stuIdList = trainPlanStudentMapper.getAllStuId(planId);
            if (stuIdList.isEmpty()){
                return MsgRespond.fail("当前培训计划尚未分配人员，无法发布课程");
            }
            for (Integer j:stuIdList){
                JSONObject res = progressClient.insertProgress(id,j,over_chapter,chapterSum);
                if (Objects.equals(res.getInteger("code"), 5005)){
                    return MsgRespond.fail(res.getString("msg"));
                }
            }
        }
        return i>0?MsgRespond.success("已成功发布课程"):MsgRespond.fail("发布失败");
    }


    /**
     * 判断课程名是否已经存在的具体实现
     * @param name 课程名
     * @return 根据处理结果返回对应消息
     */
    private String CheckExit(String name){
        Integer CheckMark = lessonMapper.getIdByL_Name(name);
        if (Objects.nonNull(CheckMark)){
            return "该课程已经存在！";
        }
        return "";
    }
    /**
     * 根据id获取指定用户的具体实现
     * @param id 课程名
     * @return 根据处理结果返回对应消息
     */
    private User getUser(int id){
        JSONObject res = userClient.getUserAccountByUid(id);
        if (Objects.equals(res.getInteger("code"), 5005)){
            return null;
        }
        JSONObject data = res.getJSONObject("data");
        int uid = data.getInteger("id");
        String realName = data.getString("realName");
        String mobile = data.getString("mobile");
        int auth_id = data.getInteger("authId");
        JSONObject Auth = data.getJSONObject("auth");
        return new User(uid,realName,mobile,auth_id,null,new User.Auth(auth_id,Auth.getString("authName")));
    }
    /**
     * 根据id判断用户是否存在的具体实现
     * @param id 教师id
     * @return 根据处理结果返回对应消息
     */
    private String UserExit(int id){
        JSONObject res = userClient.getUserAccountByUid(id);
        if (Objects.equals(res.getInteger("code"), 5005)){
            return res.getString("msg");
        }
        return "";

    }
}
