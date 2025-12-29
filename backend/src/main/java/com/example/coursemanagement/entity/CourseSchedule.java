package com.example.coursemanagement.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 课程表实体类
 */
@Data
public class CourseSchedule {

    private Integer scheduleId;
    private Integer courseId;
    private String courseName;
    private Integer semesterId;
    private Integer weekDay;
    private Integer classSection;
    private String startTime;
    private String endTime;
    private String classroom;
    private String teacher;
    private String className;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}