package com.example.coursemanagement.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 试卷实体类
 */
@Data
public class ExamPaper {

    private Integer paperId;
    private String paperName;
    private Integer courseId;
    private Double totalScore;
    private String paperType;
    private String createTeacher;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
