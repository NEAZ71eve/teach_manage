package com.example.coursemanagement.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 题目选项实体类
 */
@Data
public class QuestionOption {

    private Integer optionId;
    private Integer questionId;
    private String optionText;
    private Integer optionOrder;
    private Integer isCorrect;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}