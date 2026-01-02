package com.example.coursemanagement.controller;

import com.example.coursemanagement.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 统计Controller
 */
@RestController
@RequestMapping("/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    /**
     * 获取课程统计信息
     */
    @GetMapping("/course")
    public ResponseEntity<Map<String, Object>> getCourseStatistics() {
        return ResponseEntity.ok(statisticsService.getCourseStatistics());
    }

    /**
     * 获取知识点覆盖率统计
     */
    @GetMapping("/knowledge-point")
    public ResponseEntity<Map<String, Object>> getKnowledgePointCoverage() {
        return ResponseEntity.ok(statisticsService.getKnowledgePointCoverage());
    }

    /**
     * 获取题库统计信息
     */
    @GetMapping("/question-bank")
    public ResponseEntity<Map<String, Object>> getQuestionBankStatistics() {
        return ResponseEntity.ok(statisticsService.getQuestionBankStatistics());
    }

    /**
     * 获取试卷统计信息
     */
    @GetMapping("/exam-paper")
    public ResponseEntity<Map<String, Object>> getExamPaperStatistics() {
        return ResponseEntity.ok(statisticsService.getExamPaperStatistics());
    }
}
