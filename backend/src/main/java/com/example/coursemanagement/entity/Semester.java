package com.example.coursemanagement.entity;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 学期实体类
 */
@Data
public class Semester {

    private Integer semesterId;
    private String semesterName;
    private String semesterCode;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private Boolean isCurrent;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    // 关联的课程列表 
    private List<Course> courses;
}