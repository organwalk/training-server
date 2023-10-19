package com.training.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 自定义响应实体
 * by organwalk 2023-10-19
 */
@Data
@AllArgsConstructor
public class Respond {
    private Integer code;
    private String msg;
}
