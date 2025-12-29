package com.example.coursemanagement.entity;

import lombok.Data;

/**
 * 试卷题目关联实体类
 */
@Data
public class ExamPaperQuestion {

    private Integer id;
    private String paperId;
    private String questionId;
    private Integer questionOrder;
    private Double score;
}
