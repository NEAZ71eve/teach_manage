package com.example.coursemanagement.entity;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 题目分类实体类
 */
@Data
public class QuestionCategory {

    private Integer categoryId;
    private String categoryName;
    private String description;
    private Integer parentCategoryId;
    private Integer categoryOrder;
    private Integer isActive;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    // 关联字段
    private List<QuestionCategory> children;
    private String parentCategoryName;
}