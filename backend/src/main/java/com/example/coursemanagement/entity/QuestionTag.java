package com.example.coursemanagement.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 题目标签实体类
 */
@Data
public class QuestionTag {

    private Integer tagId;
    private String tagName;
    private String tagDescription;
    private Integer isActive;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}