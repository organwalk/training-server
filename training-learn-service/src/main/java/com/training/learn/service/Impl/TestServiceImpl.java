package com.training.learn.service.Impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.training.common.entity.*;
import com.training.learn.client.PlanClient;
import com.training.learn.client.ProgressClient;
import com.training.learn.client.UserClient;
import com.training.learn.entity.msg.ReleaseTestPushMsg;
import com.training.learn.entity.msg.ReplyLikeMsg;
import com.training.learn.entity.msg.TestMsg;
import com.training.learn.entity.request.*;

import com.training.learn.entity.result.*;
import com.training.learn.entity.table.*;
import com.training.learn.mapper.*;
import com.training.learn.producer.EventProcessMsgProducer;
import com.training.learn.reposoty.QuestionCache;
import com.training.learn.reposoty.TestCache;
import com.training.learn.service.TestService;
import com.training.learn.utils.ComputeUtil;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.*;
import java.util.stream.IntStream;

/**
 * by zhaozhifeng 2023-11-20
 * by organwalk 2023-11-25
 */
@Service
@AllArgsConstructor
@Transactional
public class TestServiceImpl implements TestService {

    private final TestMapper testMapper;
    private final QuestionMapper questionMapper;
    private final OptionMapper optionMapper;
    private final ScoreMapper scoreMapper;
    private final AnswerMapper answerMapper;
    private final TypeMapper typeMapper;
    private final UserClient userClient;
    private final PlanClient planClient;
    private final QuestionCache questionCache;
    private final TestCache testCache;
    private final EventProcessMsgProducer eventProcessMsgProducer;
    private final ProgressClient progressClient;


    /**
     * 创建试题
     *
     * @param req 请求实体
     * @return 根据处理结果返回对应消息
     * 2023/11/14
     */
    @Override
    public MsgRespond creatTest(TestReq req) {

        //判断教师是否是该课程的授课教师
        String TeaInLessonMark = judgeTeaInInLesson(req.getTeacher_id(), req.getLesson_id());
        if (!TeaInLessonMark.isBlank()) {
            return MsgRespond.fail(TeaInLessonMark);
        }

        //判断测试名称是否已经存在
        Test test = testMapper.judgeTitleExit(req.getTest_title());
        if (test != null) {
            return MsgRespond.fail("该试题已经存在！");
        }

        //判断起始和截止时间的合法性
        String validDateTimeMsg = validDateTime(req.getStart_datetime(), req.getEnd_datetime());
        if (!validDateTimeMsg.isBlank()) {
            return MsgRespond.fail(validDateTimeMsg);
        }

        req.setIsRelease(0);

        Integer i = testMapper.creatTest(req);
        return i > 0 ? MsgRespond.success("已成功创建此试卷") : MsgRespond.fail("创建试卷失败！");
    }

    @SneakyThrows
    private String validDateTime(String startDatetime, String endDatetime) {
        SimpleDateFormat si = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //判断结束时间是否早于起始时间
        if (si.parse(startDatetime).getTime() > si.parse(endDatetime).getTime()) {
            return "结束时间不得早于起始时间！";
        }
        //判断起始时间是否早于现在
        if (si.parse(startDatetime).getTime() < System.currentTimeMillis()) {
            return "起始时间不得早于现在！";
        }
        return "";
    }


    /**
     * 为试题添加题目
     *
     * @param req 请求实体
     * @return 根据处理结果返回对应消息
     * 2023/11/14
     */
    @Override
    public MsgRespond creatQuestion(int test_id, QuestionReq req) {
        //判断测试是否已经存在
        Test test = testMapper.getTestById(test_id);
        if (test == null) {
            return MsgRespond.fail("该测试不存在！");
        }
        //判断测试题是否已经存在，存在则无法修改
        List<Integer> judgeQuestionMark = questionMapper.getIdByTestId(test_id);
        if (!judgeQuestionMark.isEmpty()) {
            return MsgRespond.fail("无法二次分配试题。如需修改，请在删除本试卷后重新分配试题");
        }
        //判断试题分数是否等于100
        double score = getAllScore(req);
        if (score > 100) {
            return MsgRespond.fail("无法分配试题。当前试题总分为" + score + "分，请减少若干题目，使总分为100分");
        }
        if (score < 100) {
            return MsgRespond.fail("无法分配试题。当前试题总分为" + score + "分，请添加若干题目，使总分为100分");
        }
        for (QuestionReq.Question question : req.getQuestions()) {
            QuestionTable questionTable = new QuestionTable(null, question.getQuestion().getContent(), question.getQuestion().getImportance_id(), question.getQuestion().getTrue_answer(), test_id);
            questionMapper.insertQuestion(questionTable);
            String jsonString = JSON.toJSONString(question.getOptions());
            optionMapper.insertOption(jsonString, question.getIs_more(), questionTable.getId());
        }
        questionCache.deleteQuestion(test_id);
        return MsgRespond.success("已成功编辑试卷");
    }


    /**
     * 设置试卷状态为发布
     *
     * @param test_id 测试id
     * @return 根据处理结果返回对应消息
     * 2023/11/14
     */
    @SneakyThrows
    @Override
    public MsgRespond publishTest(int test_id) {

        Integer idMark = questionMapper.getQuestionByTestId(test_id);
        if (Objects.isNull(idMark)) {
            return MsgRespond.fail("当前试卷未完成编写，无法进行发布");
        }
        //判断测试是否存在
        Test test = testMapper.getTestById(test_id);
        if (test == null) {
            return MsgRespond.fail("该测试不存在！");
        }
        //判断试题是否已经发布
        if (test.getIsRelease() == 1) {
            return MsgRespond.fail("该测试已经发布！");
        }
        //获取当前时间，并把当前时间当成发布时间
        String date = getNowTime();
        SimpleDateFormat si = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //判断结束时间是否早于起始时间
        if (si.parse(test.getStart_datetime()).getTime() < si.parse(date).getTime()
                || si.parse(test.getEnd_datetime()).getTime() < si.parse(date).getTime()) {
            return MsgRespond.fail("发布时间不能晚于考试时间，请修改考试时间后重试");
        }
        // 判断是否存在时间冲突考试
        Test timeConflictObj = testMapper.getTimeConflictTest(test.getStart_datetime(), test.getEnd_datetime());
        if (Objects.nonNull(timeConflictObj)) {
            return MsgRespond.fail("发布失败！原因：与考试《" + timeConflictObj.getTest_title() + "》时间冲突，" + timeConflictObj.getStart_datetime() + "至" + timeConflictObj.getEnd_datetime());
        }
        testMapper.updateIsRelease(test_id, date, 1);

        JSONObject res = planClient.getLessonInfo(test.getLesson_id());
        if (Objects.equals(res.getInteger("code"), 2002)){
            JSONObject proRes = progressClient.getStudentIdList(test.getLesson_id()).join();
            if (Objects.equals(proRes.getInteger("code"), 2002)){
                eventProcessMsgProducer.triggerReleaseTestPush(
                        new ReleaseTestPushMsg(
                                String.valueOf(UUID.randomUUID()),
                                test.getTeacher_id(),
                                new ReleaseTestPushMsg.Content(res.getJSONObject("data").getString("lesson_name")),
                                test_id,
                                proRes.getObject("data", new TypeReference<List<Integer>>(){})
                        )
                );
            }
        }

        return MsgRespond.success("已成功发布试卷");
    }


    /**
     * 获取讲师的试卷列表
     *
     * @param lesson_id  课程id
     * @param teacher_id 教师id
     * @return 根据处理结果返回对应消息
     * 2023/11/14
     */
    @Override
    public DataRespond getListByLessonIdAndTeaId(int lesson_id, int teacher_id, int page_size, int offset, String auth) {
        //判断教师身份
        String TeaMark = judgeTea(teacher_id);
        if (!TeaMark.isBlank()) {
            return new DataFailRespond(TeaMark);
        }
        //判断教师是否为该课程教师
        String TeaInLessonMark = judgeTeaInInLesson(teacher_id, lesson_id);
        if (!TeaInLessonMark.isBlank()) {
            return new DataFailRespond(TeaInLessonMark);
        }
        //当身份为教师时，获取本人的测试
        if (Objects.equals(auth, "teacher")) {
            Integer sum = testMapper.getCountOfListByLessonIdAndTeacherId(lesson_id, teacher_id);
            if (sum == 0) {
                return new DataFailRespond("当前试卷列表为空");
            }
            List<Test> list = testMapper.getListByLessonIdAndTeacherId(lesson_id, teacher_id, page_size, offset);
            return new DataPagingSuccessRespond("已成功获取试卷列表", sum, list);
        }
        //当身份为管理员时，获取所有测试
        if (Objects.equals(auth, "admin")) {
            Integer sum = testMapper.getCountByLessonId(lesson_id);
            if (sum == 0) {
                return new DataFailRespond("当前试卷列表为空");
            }
            List<Test> list = testMapper.getAllTestByLessonId(lesson_id, page_size, offset);
            return new DataPagingSuccessRespond("已成功获取试卷列表", sum, list);
        }
        return new DataFailRespond("获取失败！");
    }


    /**
     * /获取试卷的所有试题
     *
     * @param test_id 测试id
     * @param auth    权限
     * @return 根据处理结果返回对应消息
     * 2023/11/14
     */
    @Override
    public DataRespond getQuestion(int test_id, String auth) {
        //判断测试的存在性
        Test test = testMapper.getTestById(test_id);
        if (test == null) {
            return new DataFailRespond("该试卷不存在！");
        }

        //判断请求用户为普通员工时
        if (Objects.equals(auth, "none")) {
            if (test.getIsRelease() == 0) {
                return new DataFailRespond("考生无法查看尚未发布的试卷");
            }
            // 校验考试时间
            String msg = validTestTime(test.getStart_datetime(), test.getEnd_datetime());
            if (!msg.isBlank()) return new DataFailRespond(msg);
            // 获取缓存
            List<StuQuestionResult.Question> questionList = questionCache.getStuQuestion(test_id, auth);
            if (Objects.isNull(questionList) || questionList.isEmpty()) {
                questionList = new ArrayList<>();
                List<Integer> allQuestionIdList = questionMapper.getIdByTestId(test_id);
                for (Integer i : allQuestionIdList) {
                    QuestionTable questionTable = questionMapper.getQuestionById(i);
                    OptionsTable optionsTable = optionMapper.getOptionByQuesId(i);

                    Map<String, String> options = JSON.parseObject(optionsTable.getOption(), new TypeReference<>() {
                    });
                    StuQuestionResult.Question question = new StuQuestionResult.Question(i, questionTable.getQuestion_content(), options, optionsTable.getIsMore());

                    questionList.add(question);
                }
                questionCache.saveStuQuestion(test_id, auth, questionList);
            }
            StuQuestionResult stuQuestionResult = new StuQuestionResult(test.getTest_title(), test.getStart_datetime(), test.getEnd_datetime(), questionList);

            return new DataSuccessRespond("已成功获取试题列表", stuQuestionResult);
        }

        //判断请求用户为教师时
        if (Objects.equals(auth, "teacher")) {
            //获取缓存数据
            List<TeaQuestionResult.Question> questionList = questionCache.getTeaQuestion(test_id, auth);
            //判断获取的缓存为空时
            if (Objects.isNull(questionList) || questionList.isEmpty()) {
                // 如果处于未发布状态，说明暂存试题不存在或已过期
                questionList = new ArrayList<>();

                List<Integer> allQuestionIdList = questionMapper.getIdByTestId(test_id);
                //获取测试详细信息
                for (Integer i : allQuestionIdList) {
                    QuestionTable questionTable = questionMapper.getQuestionById(i);
                    OptionsTable optionsTable = optionMapper.getOptionByQuesId(i);

                    Map<String, String> options = JSON.parseObject(optionsTable.getOption(), new TypeReference<>() {
                    });
                    TeaQuestionResult.QuestionDetail questionDetail = new TeaQuestionResult.QuestionDetail(i, questionTable.getQuestion_content(), questionTable.getImportance_id(), questionTable.getTrue_answer());
                    TeaQuestionResult.Question question = new TeaQuestionResult.Question(questionDetail, options, optionsTable.getIsMore());

                    questionList.add(question);
                }

                //将试题保存至缓存
                questionCache.saveTeaQuestion(test_id, auth, questionList);
            }

            TeaQuestionResult teaQuestionResult = new TeaQuestionResult(test.getTest_title(), test.getStart_datetime(), test.getEnd_datetime(), questionList);
            return new DataSuccessRespond("已成功获取试题列表", teaQuestionResult);
        }
        return new DataFailRespond("获取失败！");
    }


    /**
     * 删除指定试题
     *
     * @param test_id 试题id
     * @return 根据处理结果返回对应消息
     * 2023/11/15
     */
    @Override
    public MsgRespond deleteQuestion(int test_id) throws ParseException {
        //判断测试是否存在
        Test test = testMapper.getTestById(test_id);
        if (test == null) {
            return MsgRespond.fail("测试不存在！");
        }
        SimpleDateFormat si = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date start_datetime = si.parse(test.getStart_datetime());
        Date end_datetime = si.parse(test.getEnd_datetime());
        //获取当前时间戳
        long currentTimestamp = System.currentTimeMillis();
        //判断当前时间是否在测试时间内
        if ((start_datetime.getTime() <= currentTimestamp) && (end_datetime.getTime() >= currentTimestamp)) {
            return MsgRespond.fail("考试正在进行，无法删除此试卷");
        }
        //判断当前时间是否时测试开始的前五分钟
        long new_start_datetime = start_datetime.getTime() - 5 * 60 * 1000;
        if ((new_start_datetime <= currentTimestamp) && (currentTimestamp <= start_datetime.getTime())) {
            return MsgRespond.fail("考试即将开始，无法删除此试卷");
        }
        //测试当前是否时测试结束的后五分钟
        long new_end_datetime = end_datetime.getTime() + 5 * 60 * 1000;
        if ((end_datetime.getTime() <= currentTimestamp) && (new_end_datetime >= currentTimestamp)) {
            return MsgRespond.fail("考试结果正在处理中，无法删除此试卷");
        }

        //删除该测试信息
        testMapper.deleteTestById(test_id);
        //删除测试缓存
        if (questionCache.judgeKeyExit(test_id)) {
            questionCache.deleteQuestion(test_id);
        }
        if (questionCache.judgeStuKeyExit(test_id)) {
            questionCache.deleteStuQuestion(test_id);
        }
        return MsgRespond.success("已成功删除此试卷");
    }


    /**
     * 学生提交试题
     *
     * @param test_id    测试id
     * @param student_id 学生id
     * @return 根据处理结果返回对应消息
     * by organwalk 2023-12-25
     */
    @Override
    public MsgRespond StuSubmitQuestion(int test_id, int student_id, AnswerRequest answerRequest) throws ParseException {
        SimpleDateFormat si = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long currentTimestamp = System.currentTimeMillis();

        //判断测试是否存在
        String testTime = testCache.getTestTimeCache(test_id);
        if (testTime.isBlank()) {
            Test test = testMapper.getTestById(test_id);
            if (test == null) {
                return MsgRespond.fail("该试题不存在！");
            }
            testCache.cacheTestDateTime(test_id, test.getStart_datetime(), test.getEnd_datetime(), test.getLesson_id());
            testTime = test.getStart_datetime() + "_" + test.getEnd_datetime() + "__" + test.getLesson_id();
        }

        Date start_datetime = si.parse(testTime.split("_")[0]);
        Date end_datetime = si.parse(testTime.split("_")[1]);

        //判断当前时间是否早于测试开始时间
        if (start_datetime.getTime() > currentTimestamp) {
            return MsgRespond.fail("考试尚未开始，无法进行交卷");
        }
        //判断当前时间是否晚于测试结束时间
        if (currentTimestamp > end_datetime.getTime()) {
            return MsgRespond.fail("考试已经结束，无法进行交卷");
        }

        // 触发生产考试结果处理消息至队列
        eventProcessMsgProducer.triggerTestProcess(new TestMsg(
                String.valueOf(UUID.randomUUID()),
                getNowTime(),
                test_id,
                student_id,
                Integer.valueOf(testTime.split("__")[1]),
                answerRequest
        ));
        return MsgRespond.success("已提交处理");
    }


    /**
     * 学员查看考试结果
     *
     * @param test_id    测试id
     * @param student_id 学生id
     * @return 根据处理结果返回对应消息
     * 2023/11/14
     */
    @Override
    public DataRespond getResultOfTest(int test_id, int student_id) {
        //判断该学生是否有成绩
        ScoreTable scoreTable = scoreMapper.getByTestIdAndStuId(test_id, student_id);
        if (scoreTable == null) {
            return new DataSuccessRespond("该学员暂无成绩", new RankResult(0, "缺考", 0));
        } else if (scoreTable.getComposite_score() == 0) {
            return new DataSuccessRespond("该学员为零分卷", new RankResult(0, "零分卷", 0));
        }

        //获取评估级别
        String level = judgeLevel(scoreTable.getComposite_score());
        List<Integer> rankList = scoreMapper.getCompositeScoreRank(test_id);
        Integer rank = IntStream.range(0, rankList.size())
                .filter(i -> Objects.equals(rankList.get(i), scoreTable.getComposite_score()))
                .findFirst().orElse(0) + 1;
        return new DataSuccessRespond("成功获取该考试结果", new RankResult(scoreTable.getComposite_score(), level, rank));
    }


    /**
     * 获取指定测试中学员的评估报告
     *
     * @param test_id 测试id
     * @return 根据处理结果返回对应消息
     * 2023/11/15
     */
    @Override
    public DataRespond TeaGetAllScore(int test_id, int page_size, int offset) {
        Integer sumMark = scoreMapper.countAllScoreByTestId(test_id);
        if (Objects.isNull(sumMark)) {
            return new DataFailRespond("未能成功获取指定测试的成绩列表");
        }
        //获取指定测试的成绩列表
        List<ScoreTable> scoreTables = scoreMapper.getAllScoreByTestId(test_id, page_size, offset);
        List<ScoreResult> scoreResultList = new ArrayList<>();
        //循环获取课程下每个学生姓名，成绩，排名等
        for (ScoreTable scoreTable : scoreTables) {
            //获取学生真实姓名
            String realName = getRealName(scoreTable.getStudent_id());
            ScoreResult scoreResult = getScoreResult(scoreTable, realName);
            scoreResultList.add(scoreResult);
        }

        return new DataPagingSuccessRespond("已成功返回评估报告列表", sumMark, scoreResultList);
    }

    @NotNull
    private ScoreResult getScoreResult(ScoreTable scoreTable, String realName) {
        ScoreResult.student_info user_info = new ScoreResult.student_info(scoreTable.getStudent_id(), realName);
        //获取学生评估等级
        String level = judgeLevel(scoreTable.getComposite_score());
        ScoreResult.score score = new ScoreResult.score(scoreTable.getComposite_score(), scoreTable.getMust_composite_score(), scoreTable.getImportance_type_composite_score(), scoreTable.getNormal_type_composite_score(), level);
        return new ScoreResult(user_info, score);
    }


    /**
     * 获取测验的各项平均成绩列表
     *
     * @param lesson_id 课程id
     * @return 根据处理结果返回对应消息
     * 2023/11/14
     */
    @Override
    public DataRespond getLessonAllScore(int lesson_id, int page_size, int offset) {
        Integer sumMark = testMapper.countAllIdByLessonId(lesson_id);
        if (Objects.isNull(sumMark)) {
            return new DataFailRespond("未能获取到测试列表");
        }
        List<Integer> allTestIdList = testMapper.getAllIdByLessonId(lesson_id, page_size, offset);
        List<LessonScoreResult> list = new ArrayList<>();
        for (Integer i : allTestIdList) {
            AvgScore avgScore = scoreMapper.getAvgCompositeScoreByTestId(i);
            if (Objects.nonNull(avgScore)) {
                list.add(getLessonScoreResult(avgScore, i));
            } else {
                list.add(new LessonScoreResult(i, 0.0, 0.0, 0.0, 0.0));
            }
        }
        return new DataPagingSuccessRespond("已成功获得该课程下的各项测验平均成绩列表", sumMark, list);
    }

    private LessonScoreResult getLessonScoreResult(AvgScore avgScore, Integer i) {
        double composite_score = getDouble(avgScore.getAvgComScore());
        double must_composite_score = getDouble(avgScore.getAvgMustComScore());
        double importance_composite_score = getDouble(avgScore.getAvgImpComScore());
        double normal_composite_score = getDouble(avgScore.getAvgNorComScore());
        return new LessonScoreResult(i, composite_score, must_composite_score, importance_composite_score, normal_composite_score);
    }

    private double getDouble(Double inputDouble) {
        return inputDouble == null ? 0.0 : inputDouble;
    }


    /**
     * 编辑试卷信息
     *
     * @param id 测试id
     * @return 根据处理结果返回对应消息
     * 2023/11/15
     */
    @Override
    public MsgRespond updateTest(UpdateTestReq updateTestReq, int id) throws ParseException {
        //判断测试是否存在
        Test test = testMapper.getTestById(id);
        if (test == null) {
            return MsgRespond.fail("该测试不存在！");
        }
        SimpleDateFormat si = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date start_time = si.parse(updateTestReq.getStart_datetime());
        Date end_time = si.parse(updateTestReq.getEnd_datetime());

        long currentTimestamp = System.currentTimeMillis();
        //判断起始时间是否晚于结束时间
        if (start_time.getTime() > end_time.getTime()) {
            return MsgRespond.fail("结束时间不得早于开始时间！");
        }
        //判断试卷是否发布，如果未发布，则起始时间必须晚于现在
        if (test.getIsRelease() == 0 && start_time.getTime() < currentTimestamp) {
            return MsgRespond.fail("考试开始时间不得早于现在！");
        }
        //如果已经发布，则考试发布时间不可晚于考试开始时间
        if (test.getIsRelease() == 1 && si.parse(test.getCreat_datetime()).getTime() > start_time.getTime()) {
            return MsgRespond.fail("考试开始时间不可早于试卷的发布时间");
        }
        testMapper.updateTest(updateTestReq.getTest_title(), updateTestReq.getStart_datetime(), updateTestReq.getEnd_datetime(), id);
        return MsgRespond.success("已成功编辑此试卷信息");
    }


    /**
     * 暂时保存讲师编写的试卷
     *
     * @param test_id 测试id
     * @param req     请求实体
     * @return 根据处理结果返回对应消息
     * 2023/11/15
     */
    @Override
    public MsgRespond saveCache(int test_id, CacheReq req) {
        //判断测试是否存在
        Test test = testMapper.getTestById(test_id);
        if (test == null) {
            return MsgRespond.fail("该试卷不存在！");
        }
        //判断该测试下是否已经有试题
        Integer idMark = questionMapper.getQuestionByTestId(test_id);
        if (Objects.nonNull(idMark)) {
            return MsgRespond.fail("试题已正式提交，无需暂存");
        }

        //判断是否修改
        if ((!Objects.equals(test.getTest_title(), req.getTest_title())) || (!Objects.equals(test.getStart_datetime(), req.getStart_datetime())) || (!Objects.equals(test.getEnd_datetime(), req.getEnd_datetime()))) {
            testMapper.updateTest(req.getTest_title(), req.getStart_datetime(), req.getEnd_datetime(), test_id);
        }

        //获取暂存缓存
        List<CacheReq.Question> list = questionCache.getCache(test_id);
        //判断暂存缓存是否为空，非空则删除
        if (Objects.nonNull(list)) {
            questionCache.deleteQuestion(test_id);
        }

        //保存新的暂存试卷
        questionCache.saveCache(test_id, req);
        return MsgRespond.success("暂存成功。暂存时限为七日，七日后若试卷未作任何进一步编辑，则自动销毁");
    }

    @Override
    public DataRespond getTestInfo(Integer id) {
        return new DataSuccessRespond("已成功获取试卷基本信息", testMapper.getTestInfo(id));
    }

    @Override
    public DataRespond getTestPaperListByLessonIdAndTeaId(Integer lesson_id, Integer teacher_id, Integer type) {
        switch (type) {
            case 0 -> {
                // 全部试卷
                List<Test> testPaper = testMapper.selectAllReleaseTestPaper(lesson_id, teacher_id);
                return testPaper.isEmpty()
                        ? new DataFailRespond("该教师尚未在此课程发布考试")
                        : new DataSuccessRespond("已成功获取试卷列表", testPaper);
            }
            case 1 -> {
                // 待考试
                List<Test> testPaper = testMapper.selectWaitingTestPaper(lesson_id, teacher_id);
                return testPaper.isEmpty()
                        ? new DataFailRespond("尚无考试待开始")
                        : new DataSuccessRespond("已成功获取待考试的试卷列表", testPaper);
            }
            case 2 -> {
                // 已结束
                List<Test> testPaper = testMapper.selectOverTestPaper(lesson_id, teacher_id);
                return testPaper.isEmpty()
                        ? new DataFailRespond("尚无已结束的考试")
                        : new DataSuccessRespond("成功获取已结束考试的试卷列表", testPaper);

            }
        }
        return new DataFailRespond("type变量只能为枚举数字，范围为：0-全部考试，1-待考试，2-已结束");
    }

    @Override
    public DataRespond getIsOverTestPaperIdList(Integer studentId, Integer testId) {
        Integer countMark = answerMapper.countOverTest(studentId, testId);
        if (Objects.isNull(countMark)) {
            return new DataSuccessRespond("未完成作答", false);
        }
        return new DataSuccessRespond("已完成作答", true);
    }


    /**
     * 判断用户是否为教师
     *
     * @param teacher_id 教师id
     * @return 根据处理结果返回对应消息
     * 2023/11/14
     */
    private String judgeTea(int teacher_id) {
        JSONObject req = userClient.getUserAccountByUid(teacher_id);
        JSONObject data = req.getJSONObject("data");
        Integer authId = data.getInteger("authId");
        if (Objects.equals(authId, 2)) {
            return "";
        }
        return "该用户不是教师！";
    }


    /**
     * 判断教师是否是该课程的教师
     *
     * @param teacher_id 教师id
     * @param lesson_id  课程id
     * @return 根据处理结果返回对应消息
     * 2023/11/14
     */
    private String judgeTeaInInLesson(int teacher_id, int lesson_id) {
        JSONObject req = planClient.getLessonInfo(lesson_id);
        if (Objects.equals(req.getInteger("code"), 5005)) {
            return "请求数据有误";
        }
        JSONObject data = req.getJSONObject("data");
        Integer TeaId = data.getInteger("teacher_id");
        if (Objects.equals(TeaId, teacher_id)) {
            return "";
        }
        return "该教师不是此课程教师！";
    }


    /**
     * 获取当前时间
     *
     * @return 根据处理结果返回对应消息
     * 2023/11/14
     */
    private String getNowTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        return now.format(formatter);
    }


    /**
     * 获取试卷试题的总分
     *
     * @return 根据处理结果返回对应消息
     * 2023/11/14
     */
    private double getAllScore(QuestionReq req) {
        Integer must_score = typeMapper.getScoreById(1);
        Integer important_score = typeMapper.getScoreById(2);
        Integer general_score = typeMapper.getScoreById(3);
        return ComputeUtil.judgeScore(must_score, important_score, general_score, req);
    }


    /**
     * 获取用户真实姓名
     *
     * @param student_id 学生id
     * @return 根据处理结果返回对应消息
     * 2023/11/15
     */
    private String getRealName(int student_id) {
        JSONObject req = userClient.getUserAccountByUid(student_id);
        JSONObject data = req.getJSONObject("data");
        return data.getString("realName");
    }


    /**
     * 判断成绩等级
     *
     * @param score 成绩
     * @return 根据处理结果返回对应消息
     * 2023/11/15
     */
    private String judgeLevel(Integer score) {
        String level;
        if (score >= 80) {
            level = "优秀";
        } else if (score >= 60) {
            level = "良好";
        } else {
            level = "不及格";
        }
        return level;
    }

    @SneakyThrows
    private String validTestTime(String startDateTime, String endDateTime) {
        SimpleDateFormat si = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //判断当前时间是否早于起始时间时
        if (si.parse(startDateTime).getTime() > System.currentTimeMillis()) {
            return "考试尚未开始，无法查看试卷题目";
        }
        //判断当前时间是否晚于起始时间
        if (si.parse(endDateTime).getTime() < System.currentTimeMillis()) {
            return "考试已经结束，无法查看试卷题目";
        }
        return "";
    }


}
