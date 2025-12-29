package com.example.coursemanagement.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 课程实体类
 */
@Getter
@Setter
@ToString(exclude = "semesters")
public class Course {

    private Integer courseId;
    private String courseName;
    private String courseCode;
    private Integer majorId;
    private Integer programId;
    private Double credit;
    private Integer totalHours;
    private Integer theoreticalHours;
    private Integer practicalHours;
    private String courseType;
    private String courseNature;
    private String examMark;
    private String courseCategory;
    private String description;
    private String teacherIds;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    // 关联的学期列表
    private List<Semester> semesters;
}
