package com.training.plan.entity.respond;

import com.training.plan.entity.result.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StudentInfo {
    private int id;
    private int training_student_id;
    private User user;
}
