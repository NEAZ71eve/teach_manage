package com.example.coursemanagement.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 知识点实体类
 */
@Data
public class KnowledgePoint {
    private String pointId;
    private String pointName;
    private String courseId;
    private String programId;
    private String parentId;
    private String description;
    private String difficulty;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
