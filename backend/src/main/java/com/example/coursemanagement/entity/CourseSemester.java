package com.example.coursemanagement.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 课程学期关联实体类
 */
@Data
public class CourseSemester {

    private Integer id;
    private Integer courseId;
    private Integer semesterId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}