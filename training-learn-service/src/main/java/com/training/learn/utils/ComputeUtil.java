package com.training.learn.utils;


import com.training.learn.entity.request.QuestionReq;
import com.training.learn.entity.result.ProgressLesson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

//计算工具类
public class ComputeUtil {


    //计算一个课程的学习进度 传入t_progress_lesson表的list 计算平均的学习进度为百分比
     public static double comProgress(List<ProgressLesson> list){
        //课程完成进度总累加
        double sum = 0;
        //遍历list
        for(ProgressLesson progressLesson : list){
            //计算每个对象的进度，完成的除以总数
            double progress = (double) progressLesson.getOver_chapter_sum() /progressLesson.getLesson_chapter_sum();
            //计算这个课程的完成进度总数（累加）
            sum += progress;
        }
        //计算平均进度（进度累加除以学生数量）
        return sum/list.size();
    }

    public static double getStuProgress(ProgressLesson progressLesson){
         if (Objects.isNull(progressLesson)){
             return 0;
         }
        return (double) progressLesson.getOver_chapter_sum() / progressLesson.getLesson_chapter_sum();
    }

    //计算编辑的试卷总分
    public static double judgeScore(Integer must_score, Integer important_score, Integer general_score, QuestionReq list){
         double sum = 0;
        Map<Integer, Double> scoreMap = new HashMap<>();
        scoreMap.put(1, 1.0);
        scoreMap.put(2, 0.8);
        scoreMap.put(3, 0.5);
         List<QuestionReq.Question> list1 = list.getQuestions();
         for(QuestionReq.Question req:list1){
             if (Objects.equals(req.getQuestion().getImportance_id(),1)){

                 sum += must_score * scoreMap.get(1);
             }
             if (Objects.equals(req.getQuestion().getImportance_id(),2)){

                 sum += important_score * scoreMap.get(2);
             }
             if (Objects.equals(req.getQuestion().getImportance_id(),3)){

                 sum += general_score * scoreMap.get(3);
             }
         }

         return sum;
    }


}
