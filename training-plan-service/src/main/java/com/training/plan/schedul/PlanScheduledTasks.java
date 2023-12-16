package com.training.plan.schedul;

import com.training.plan.mapper.TrainingPlanMapper;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 培训计划状态定时更新
 * by organwalk 2023-12-10
 */
@Component
@AllArgsConstructor
public class PlanScheduledTasks {
    private final TrainingPlanMapper trainingPlanMapper;

    // 在服务启动时执行
    @PostConstruct
    public void executeOnStartup() {
        trainingPlanMapper.updateTrainingStateOver();
    }

    // 每日00:00执行
    @Scheduled(cron = "0 0 0 * * ?") // cron表达式表示每天的00:00触发
    public void executeDailyAtMidnight() {
        trainingPlanMapper.updateTrainingStateOver();
    }
}
