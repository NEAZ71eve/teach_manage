package com.example.coursemanagement.entity;

import lombok.Data;

/**
 * 试卷题目关联实体类
 */
@Data
public class ExamPaperQuestion {

    private Integer detailId;
    private String paperId;
    private String questionId;
    private Integer questionOrder;
    
    // 关联的完整题目信息
    private Question question;
}
