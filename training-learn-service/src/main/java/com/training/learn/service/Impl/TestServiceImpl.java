package com.training.learn.service.Impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.training.common.entity.*;
import com.training.learn.client.PlanClient;
import com.training.learn.client.UserClient;
import com.training.learn.entity.request.*;

import com.training.learn.entity.result.*;
import com.training.learn.entity.table.*;
import com.training.learn.mapper.*;
import com.training.learn.reposoty.QuestionCache;
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


    /**
     * 创建试题
     *
     * @param req 请求实体
     * @return 根据处理结果返回对应消息
     * 2023/11/14
     */
    @Override
    public MsgRespond creatTest(TestReq req) throws ParseException {
        //判断用户的教师身份
        String TeaMark = judgeTea(req.getTeacher_id());
        if (!TeaMark.isBlank()) {
            return MsgRespond.fail(TeaMark);
        }
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
        SimpleDateFormat si = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        //判断结束时间是否早于起始时间
        if (si.parse(req.getStart_datetime()).getTime() > si.parse(req.getEnd_datetime()).getTime()) {
            return MsgRespond.fail("结束时间不得早于起始时间！");
        }
        //判断起始时间是否早于现在
        if (si.parse(req.getStart_datetime()).getTime() < System.currentTimeMillis()) {
            return MsgRespond.fail("起始时间不得早于现在！");
        }

        req.setIsRelease(0);

        Integer i = testMapper.creatTest(req);
        return i > 0 ? MsgRespond.success("已成功创建此试卷") : MsgRespond.fail("创建试卷失败！");
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
        Integer score = getAllScore(req);
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
        if (Objects.isNull(idMark)){
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
        SimpleDateFormat si = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        //判断结束时间是否早于起始时间
        if (si.parse(test.getStart_datetime()).getTime() < si.parse(date).getTime()
                || si.parse(test.getEnd_datetime()).getTime() < si.parse(date).getTime()){
            return MsgRespond.fail("发布时间不能晚于考试时间，请修改考试时间后重试");
        }
        testMapper.updateIsRelease(test_id, date, 1);
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
     * @param test_id 测试id
     * @param auth    权限
     * @return 根据处理结果返回对应消息
     * 2023/11/14
     */
    @Override
    public DataRespond getQuestion(int test_id, String auth){
        //判断测试的存在性
        Test test = testMapper.getTestById(test_id);
        if (test == null) {
            return new DataFailRespond("该试卷不存在！");
        }

        //判断请求用户为普通员工时
        if (Objects.equals(auth, "none")) {

            if (test.getIsRelease() == 0){
                return new DataFailRespond("考生无法查看尚未发布的试卷");
            }

            // 校验考试时间
            String msg = validDateTime(test.getStart_datetime(), test.getEnd_datetime());
            if (!msg.isBlank()) return new DataFailRespond(msg);
            // 获取缓存
            List<StuQuestionResult.Question> questionList = questionCache.getStuQuestion(test_id, auth);
            if (Objects.isNull(questionList) || questionList.isEmpty()){
                List<Integer> allQuestionIdList = questionMapper.getIdByTestId(test_id);
                for (Integer i : allQuestionIdList) {
                    QuestionTable questionTable = questionMapper.getQuestionById(i);
                    OptionsTable optionsTable = optionMapper.getOptionByQuesId(i);

                    Map<String, String> options = JSON.parseObject(optionsTable.getOption(), new TypeReference<>() {});
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
                if (test.getIsRelease() == 0){
                    return new DataSuccessRespond("未能获取到暂存试题", new TeaQuestionResult(test.getTest_title(), test.getStart_datetime(), test.getEnd_datetime(), questionList));
                }

                List<Integer> allQuestionIdList = questionMapper.getIdByTestId(test_id);
                //获取测试详细信息
                for (Integer i : allQuestionIdList) {
                    QuestionTable questionTable = questionMapper.getQuestionById(i);
                    OptionsTable optionsTable = optionMapper.getOptionByQuesId(i);

                    Map<String, String> options = JSON.parseObject(optionsTable.getOption(), new TypeReference<>() {});
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
        SimpleDateFormat si = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
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
        //获取指定测试的所有试题id
        List<Integer> AllQuestionId = questionMapper.getIdByTestId(test_id);
        //删除指定测试的所有选项
        for (Integer i : AllQuestionId) {
            optionMapper.deleteByQuesId(i);
        }
        //删除所有学生答案信息
        answerMapper.deleteQuestionByTestId(test_id);
        //删除所有测试试题
        questionMapper.deleteQuestionByTestId(test_id);
        //删除该测试下的分数
        scoreMapper.deleteByTestId(test_id);
        //删除该测试信息
        testMapper.deleteTestById(test_id);
        //删除测试缓存
        if (questionCache.judgeKeyExit(test_id)) {
            questionCache.deleteQuestion(test_id);
        }
        if (questionCache.judgeStuKeyExit(test_id)){
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
     * 2023/11/14
     */
    @Override
    public MsgRespond StuSubmitQuestion(int test_id, int student_id, AnswerRequest answerRequest) throws ParseException {
        //判断学生身份
        String StuMark = judgeStu(student_id);
        if (!StuMark.isBlank()) {
            return MsgRespond.fail(StuMark);
        }
        //判断测试是否存在
        Test test = testMapper.getTestById(test_id);
        if (test == null) {
            return MsgRespond.fail("该试题不存在！");
        }
        //判断该学生是否有该测试的成绩
        Integer ScoreMark = scoreMapper.judgeExitByTestIdAndStuId(test_id, student_id);
        if (ScoreMark != null) {
            return MsgRespond.fail("试题已经提交");
        }

        SimpleDateFormat si = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        long currentTimestamp = System.currentTimeMillis();
        Date start_datetime = si.parse(test.getStart_datetime());
        Date end_datetime = si.parse(test.getEnd_datetime());
        //判断当前时间是否早于测试开始时间
        if (start_datetime.getTime() > currentTimestamp) {
            return MsgRespond.fail("考试尚未开始，无法进行交卷");
        }
        //判断当前时间是否晚于测试结束时间
        if (currentTimestamp > end_datetime.getTime()) {
            return MsgRespond.fail("考试已经结束，无法进行交卷");
        }

        int must_score = 0;
        //获取必须类别题详细
        TypeTable mustType = typeMapper.getTypeById(1);
        Integer must_type = mustType.getScore();
        //获取重要类别题详细
        int importance_score = 0;
        TypeTable importanceType = typeMapper.getTypeById(2);
        Integer importance_type = importanceType.getScore();
        //获取一般综合类别题详细
        int normal_score = 0;
        TypeTable normalType = typeMapper.getTypeById(3);
        Integer normal_type = normalType.getScore();

        for (AnswerRequest.Answer answer : answerRequest.getAnswers()) {
            String true_answer = questionMapper.getAnswerById(answer.getQ_id());
            Integer importanceId = questionMapper.getImportanceIdById(answer.getQ_id());
            if (Objects.equals(true_answer, answer.getAnswer())) {
                if (importanceId == 1) {
                    must_score += must_type;
                } else if (importanceId == 2) {
                    importance_score += importance_type;
                } else if (importanceId == 3) {
                    normal_score += normal_type;
                }
            }
        }
        //获取必须类别题综合得分
        double result_must_score = must_score * mustType.getWeight();
        //获取重要类别题综合得分
        double result_importance_score = importance_score * importanceType.getWeight();
        //获取一般类别题综合得分
        double result_normal_score = normal_score * normalType.getWeight();
        //获取综合得分
        double result_all_score = result_importance_score + result_must_score + result_normal_score;
        String Date = getNowTime();
        //将答案转化为json字符串
        String answer_result = JSON.toJSONString(answerRequest.getAnswers());
        //插入学生答案
        answerMapper.insertAnswer(answer_result, student_id, test_id, Date);
        //插入学生得分
        scoreMapper.insertScore(result_all_score, result_must_score, result_importance_score, result_normal_score, student_id, test_id);
        return MsgRespond.success("交卷成功！交卷时间为：" + Date + "。可在考试结束后5分钟查看考试结果");
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
        //判断测试是否存在
        Test test = testMapper.getTestById(test_id);
        if (test == null) {
            return new DataFailRespond("该测试不存在！");
        }
        //判断学生身份
        String StuMark = judgeStu(student_id);
        if (!StuMark.isBlank()) {
            return new DataFailRespond("该用户不是学生！");
        }
        //判断该学生是否有成绩
        ScoreTable scoreTable = scoreMapper.getByTestIdAndStuId(test_id, student_id);
        if (scoreTable == null) {
            return new DataFailRespond("该学员未拥有本试题成绩！");
        }
        //获取评估级别
        String level = judgeLevel(scoreTable.getComposite_score());
        //获取在该课程下的排名
        Integer rank = scoreMapper.getCompositeScoreRank(test_id, student_id);
        RankResult result = new RankResult(scoreTable.getComposite_score(), level, rank);
        return new DataSuccessRespond("成功获取该考试结果", result);
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
        //判断测试是否存在
        Test test = testMapper.getTestById(test_id);
        if (test == null) {
            return new DataFailRespond("该测试不存在！");
        }
        //获取指定测试的成绩列表
        List<ScoreTable> scoreTables = scoreMapper.getAllScoreByTestId(test_id);
        List<ScoreResult> scoreResultList = new ArrayList<>();
        //循环获取课程下每个学生姓名，成绩，排名等
        for (ScoreTable scoreTable : scoreTables) {
            //获取学生真实姓名
            String realName = getRealName(scoreTable.getStudent_id());
            ScoreResult scoreResult = getScoreResult(scoreTable, realName);
            scoreResultList.add(scoreResult);
        }
        //实现分页
        int endIndex = Math.min(offset + page_size, scoreResultList.size());
        List<ScoreResult> results = scoreResultList.subList(offset, endIndex);
        return new DataPagingSuccessRespond("已成功返回评估报告列表", scoreResultList.size(), results);
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
        //判断课程是否存在
        String LessonMark = judgeLessonExit(lesson_id);
        if (!LessonMark.isBlank()) {
            return new DataFailRespond(LessonMark);
        }
        //判断指定课程下的所有测试id
        List<Integer> allTestIdList = testMapper.getAllIdByLessonId(lesson_id);
        if (allTestIdList == null) {
            return new DataFailRespond("该课程不包含测试！");
        }

        List<LessonScoreResult> list = new ArrayList<>();
        for (Integer i : allTestIdList) {
            //综合分数平均分
            double composite_score = scoreMapper.getComposite_scoreAVGByTestId(i);
            //必须类别题型综合分数平均分
            double must_composite_score = scoreMapper.getMustCompositeScoreAVGByTestId(i);
            //重要类别题型综合分数平均分
            double importance_composite_score = scoreMapper.getImportanceCompositeScoreAVGByTestId(i);
            //普通类别题型综合分数平均分
            double normal_composite_score = scoreMapper.getNormalCompositeScoreAVGByTestId(i);
            LessonScoreResult lessonScoreResult = new LessonScoreResult(i, composite_score, must_composite_score, importance_composite_score, normal_composite_score);
            list.add(lessonScoreResult);
        }
        //实现分页查询
        int endIndex = Math.min(offset + page_size, list.size());
        List<LessonScoreResult> result = list.subList(offset, endIndex);
        return new DataPagingSuccessRespond("已成功获得该课程下的各项测验平均成绩列表", list.size(), result);
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
        SimpleDateFormat si = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date start_time = si.parse(updateTestReq.getStart_datetime());
        Date end_time = si.parse(updateTestReq.getEnd_datetime());

        long currentTimestamp = System.currentTimeMillis();
        //判断起始时间是否晚于结束时间
        if (start_time.getTime() > end_time.getTime()) {
            return MsgRespond.fail("结束时间不得早于开始时间！");
        }
        //判断试卷是否发布，如果未发布，则起始时间必须晚于现在
        if (test.getIsRelease() == 0) {
            if (start_time.getTime() < currentTimestamp) {
                return MsgRespond.fail("考试开始时间不得早于现在！");
            }
        }
        //如果已经发布，则考试发布时间不可晚于考试开始时间
        if (test.getIsRelease() == 1) {
            Date creat_time = si.parse(test.getCreat_datetime());
            if (creat_time.getTime() > start_time.getTime()) {
                return MsgRespond.fail("考试开始时间不可早于试卷的发布时间");
            }
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
            return MsgRespond.fail("该试题不存在！");
        }
        //判断该测试下是否已经有试题
        Integer idMark = questionMapper.getQuestionByTestId(test_id);
        if (Objects.nonNull(idMark)){
            return MsgRespond.fail("试题已正式提交，无法暂存");
        }
        Integer isRelease = testMapper.getReleaseState(test_id);
        if (isRelease == 1) {
            return MsgRespond.fail("该测试已经发布！");
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
        JSONObject data = req.getJSONObject("data");
        Integer TeaId = data.getInteger("teacher_id");
        if (Objects.equals(TeaId, teacher_id)) {
            return "";
        }
        return "该教师不是此课程教师！";
    }


    /**
     * 判断课程是否存在
     *
     * @param lesson_id 课程id
     * @return 根据处理结果返回对应消息
     * 2023/11/15
     */
    private String judgeLessonExit(int lesson_id) {
        JSONObject req = planClient.getLessonInfo(lesson_id);
        if (Objects.equals(req.get("code"), 5005)) {
            return "该课程不存在！";
        }
        return "";
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
    private Integer getAllScore(QuestionReq req) {
        Integer must_score = typeMapper.getScoreById(1);
        Integer important_score = typeMapper.getScoreById(2);
        Integer general_score = typeMapper.getScoreById(3);
        return ComputeUtil.judgeScore(must_score, important_score, general_score, req);
    }


    /**
     * 判断用户是否是学生
     *
     * @param student_id 学生id
     * @return 根据处理结果返回对应消息
     * 2023/11/15
     */
    private String judgeStu(int student_id) {
        JSONObject req = userClient.getUserAccountByUid(student_id);
        JSONObject data = req.getJSONObject("data");
        if (data.getInteger("authId") == 1) {
            return "";
        }
        return "该用户不是学生！";
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
    private String validDateTime(String startDateTime, String endDateTime) {
        SimpleDateFormat si = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
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
