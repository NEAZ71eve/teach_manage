package com.example.coursemanagement.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 培养方案实体类
 */
@Data
public class TrainingProgram {

    private Integer programId;
    private String majorName;
    private Integer duration;
    private Double totalCredit;
    private Integer effectiveYear;
    private Integer teacherId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
