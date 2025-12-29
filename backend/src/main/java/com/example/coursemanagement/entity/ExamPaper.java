package com.example.coursemanagement.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 试卷实体类
 */
@Data
public class ExamPaper {

    private String paperId;
    private String paperName;
    private String programId;
    private Double totalScore;
    private String createTeacher;
    private LocalDateTime createTime;
}
