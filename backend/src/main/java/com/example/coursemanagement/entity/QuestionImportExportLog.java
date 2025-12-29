package com.example.coursemanagement.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 题目导入导出日志实体类
 */
@Data
public class QuestionImportExportLog {

    private Integer logId;
    private String operationType;
    private String fileName;
    private String filePath;
    private Integer recordCount;
    private Integer successCount;
    private Integer failureCount;
    private Integer operatorId;
    private LocalDateTime operationTime;
    private String remark;
}
