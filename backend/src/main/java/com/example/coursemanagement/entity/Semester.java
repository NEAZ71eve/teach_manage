package com.example.coursemanagement.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 学期实体类
 */
@Getter
@Setter
@ToString(exclude = "courses")
public class Semester {

    private Integer semesterId;
    private String semesterName;
    private String semesterCode;
    private String startDate;
    private String endDate;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    // 关联的课程列表 
    private List<Course> courses;
}