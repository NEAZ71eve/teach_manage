package com.example.coursemanagement.service;

import java.util.Map;

/**
 * 统计服务接口
 */
public interface StatisticsService {

    /**
     * 获取课程统计信息
     * @return 课程统计数据
     */
    Map<String, Object> getCourseStatistics();

    /**
     * 获取知识点覆盖率统计
     * @return 知识点覆盖率统计数据
     */
    Map<String, Object> getKnowledgePointCoverage();

    /**
     * 获取题库统计信息
     * @return 题库统计数据
     */
    Map<String, Object> getQuestionBankStatistics();

    /**
     * 获取试卷统计信息
     * @return 试卷统计数据
     */
    Map<String, Object> getExamPaperStatistics();
}
