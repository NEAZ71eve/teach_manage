package com.example.coursemanagement.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class PracticeProject {
    private Integer projectId;
    private String title;
    private String description;
    private String publisher;    // 发布教师/管理员
    private LocalDateTime createTime;
    private LocalDateTime deadline;
}