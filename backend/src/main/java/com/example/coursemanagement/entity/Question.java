package com.example.coursemanagement.entity;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 题目实体类
 */
@Data
public class Question {

    private Integer questionId;
    private Integer questionType; // 1-单选题，2-多选题，3-判断题，4-填空题，5-简答题
    private String questionContent;
    private Integer categoryId;
    private Integer kpId;
    private String difficulty;
    private Double score;
    private String correctAnswer;
    private String analysis;
    private Integer isUsed;
    private String status;
    private Integer creatorId;
    private Integer reviewerId;
    private LocalDateTime reviewTime;
    private String reviewRemark;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    // 直接存储在question表中的选项字段
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String optionE;
    
    // 关联字段
    private String categoryName;
    private String kpName;
    private String courseName; // 所属课程名称
    private List<QuestionOption> options;
    private List<QuestionTag> tags;
    private QuestionStatistics statistics;
}
