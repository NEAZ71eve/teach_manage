package com.example.coursemanagement.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 题目统计实体类
 */
@Data
public class QuestionStatistics {

    private Integer statId;
    private Integer questionId;
    private Integer usageCount;
    private Integer correctCount;
    private Integer incorrectCount;
    private Double correctRate;
    private LocalDateTime lastUsedTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}