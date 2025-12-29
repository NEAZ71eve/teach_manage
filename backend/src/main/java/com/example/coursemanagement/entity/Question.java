package com.example.coursemanagement.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 题目实体类
 */
@Data
public class Question {

    private Integer questionId;
    private Integer courseId;
    private Integer pointId;
    private String questionType;
    private String content;
    private String options;
    private String answer;
    private String difficulty;
    private Double score;
    private Integer teacherId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
