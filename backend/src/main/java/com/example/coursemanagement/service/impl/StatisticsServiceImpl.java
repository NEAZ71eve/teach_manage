package com.example.coursemanagement.service.impl;

import com.example.coursemanagement.repository.CourseRepository;
import com.example.coursemanagement.repository.ExamPaperRepository;
import com.example.coursemanagement.repository.KnowledgePointRepository;
import com.example.coursemanagement.repository.QuestionRepository;
import com.example.coursemanagement.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 统计服务实现类
 */
@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private CourseRepository courseRepository; 

    @Autowired
    private KnowledgePointRepository knowledgePointRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private ExamPaperRepository examPaperRepository;

    @Override
    public Map<String, Object> getCourseStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        // 获取课程总数
        int totalCourses = courseRepository.count();
        statistics.put("totalCourses", totalCourses);
        // 这里可以添加更多课程统计信息，如按学期、教师、课程类型等维度的统计
        return statistics;
    }

    @Override
    public Map<String, Object> getKnowledgePointCoverage() {
        Map<String, Object> statistics = new HashMap<>();
        // 获取知识点总数
        int totalKnowledgePoints = knowledgePointRepository.count();
        // 这里可以添加知识点覆盖率统计，如各知识点的题目数量分布
        statistics.put("totalKnowledgePoints", totalKnowledgePoints);
        return statistics;
    }

    @Override
    public Map<String, Object> getQuestionBankStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        // 获取题目总数
        int totalQuestions = questionRepository.count();
        statistics.put("totalQuestions", totalQuestions);
        // 这里可以添加更多题库统计信息，如题型分布、难度分布等
        return statistics;
    }

    @Override
    public Map<String, Object> getExamPaperStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        // 获取试卷总数
        int totalPapers = examPaperRepository.count();
        statistics.put("totalPapers", totalPapers);
        // 这里可以添加更多试卷统计信息，如按课程、教师等维度的统计
        return statistics;
    }
}
