package com.training.progress.utils;

import com.training.progress.entity.table.ProgressLesson;

import java.util.List;

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
        return (double) progressLesson.getOver_chapter_sum() /progressLesson.getLesson_chapter_sum();
    }


}
