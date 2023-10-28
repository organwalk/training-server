package com.training.plan.entity.respond;

import com.training.plan.entity.result.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data

public class TeacherInfo {
    private int id;
    private int training_teacher_id;
    private User user;


    public TeacherInfo(int id,int training_teacher_id,User user) {
        this.id = id;
        this.training_teacher_id = training_teacher_id;
        this.user = user;
    }
}
