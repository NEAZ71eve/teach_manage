package com.example.coursemanagement.entity;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 知识点实体类
 */
@Data
public class KnowledgePoint {
    private Integer pointId;
    private String pointName;
    private Integer courseId;
    private Integer parentId;
    private String description;
    private String difficulty;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    // 用于树形结构展示的子知识点列表
    private List<KnowledgePoint> children;
}
