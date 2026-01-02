package com.example.coursemanagement.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 题目标签关联实体类
 */
@Data
public class QuestionTagRelation {

    private Integer relationId;
    private Integer questionId;
    private Integer tagId;
    private LocalDateTime createTime;
}
