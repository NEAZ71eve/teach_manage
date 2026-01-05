package com.example.coursemanagement.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class PracticeProject {
    private Integer id;
    private String courseCode;
    private String projectName;
    private Integer semester;
    private Integer weeks;
    private Double credit;
    private String remarks;
    private Integer teacherId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
