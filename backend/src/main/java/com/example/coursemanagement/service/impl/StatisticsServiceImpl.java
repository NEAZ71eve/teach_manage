package com.example.coursemanagement.service.impl;

import com.example.coursemanagement.repository.CourseRepository;
import com.example.coursemanagement.repository.ExamPaperRepository;
import com.example.coursemanagement.repository.KnowledgePointRepository;
import com.example.coursemanagement.repository.QuestionRepository;
import com.example.coursemanagement.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
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

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public Map<String, Object> getCourseStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        // 获取课程总数
        int totalCourses = courseRepository.count();
        statistics.put("totalCourses", totalCourses);
        // 课程平均学分
        Double avgCredit = jdbcTemplate.queryForObject("SELECT IFNULL(AVG(credit), 0) FROM course", Double.class);
        statistics.put("averageCredit", avgCredit != null ? avgCredit : 0.0);

        // 按培养方案统计课程数量
        String programSql = "SELECT tp.program_id AS programId, tp.major_name AS programName, " +
                "COUNT(c.course_id) AS courseCount " +
                "FROM training_program tp " +
                "LEFT JOIN course c ON c.program_id = tp.program_id " +
                "GROUP BY tp.program_id, tp.major_name " +
                "ORDER BY courseCount DESC";
        List<Map<String, Object>> courseByProgram = jdbcTemplate.queryForList(programSql);
        statistics.put("courseByProgram", courseByProgram);

        // 按课程性质统计
        String natureSql = "SELECT course_nature AS courseNature, COUNT(*) AS courseCount " +
                "FROM course GROUP BY course_nature ORDER BY courseCount DESC";
        List<Map<String, Object>> courseByNature = jdbcTemplate.queryForList(natureSql);
        statistics.put("courseByNature", courseByNature);
        return statistics;
    }

    @Override
    public Map<String, Object> getKnowledgePointCoverage() {
        Map<String, Object> statistics = new HashMap<>();
        // 获取知识点总数
        int totalKnowledgePoints = knowledgePointRepository.count();
        // 这里可以添加知识点覆盖率统计，如各知识点的题目数量分布
        statistics.put("totalKnowledgePoints", totalKnowledgePoints);

        String knowledgePointSql = "SELECT kp.kp_id AS kpId, kp.kp_name AS kpName, " +
                "c.course_name AS courseName, COUNT(q.question_id) AS questionCount " +
                "FROM knowledge_point kp " +
                "LEFT JOIN question q ON q.kp_id = kp.kp_id " +
                "LEFT JOIN course c ON c.course_id = kp.course_id " +
                "GROUP BY kp.kp_id, kp.kp_name, c.course_name " +
                "ORDER BY questionCount DESC";
        List<Map<String, Object>> coverageByKnowledgePoint = jdbcTemplate.queryForList(knowledgePointSql);
        statistics.put("coverageByKnowledgePoint", coverageByKnowledgePoint);

        String courseCoverageSql = "SELECT c.course_id AS courseId, c.course_name AS courseName, " +
                "COUNT(DISTINCT kp.kp_id) AS knowledgePointCount, " +
                "COUNT(q.question_id) AS questionCount " +
                "FROM course c " +
                "LEFT JOIN knowledge_point kp ON kp.course_id = c.course_id " +
                "LEFT JOIN question q ON q.kp_id = kp.kp_id " +
                "GROUP BY c.course_id, c.course_name " +
                "ORDER BY knowledgePointCount DESC";
        List<Map<String, Object>> coverageByCourse = jdbcTemplate.queryForList(courseCoverageSql);
        statistics.put("coverageByCourse", coverageByCourse);
        return statistics;
    }

    @Override
    public Map<String, Object> getQuestionBankStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        // 获取题目总数
        int totalQuestions = questionRepository.count();
        statistics.put("totalQuestions", totalQuestions);

        // 计算题型分布：按question_type分组计数
        String sql = "SELECT question_type AS type, COUNT(*) AS count FROM question GROUP BY question_type";
        List<Map<String, Object>> typeDistribution = jdbcTemplate.queryForList(sql);
        statistics.put("questionTypeDistribution", typeDistribution);

        // 计算难度分布
        String difficultySql = "SELECT difficulty AS difficulty, COUNT(*) AS count FROM question GROUP BY difficulty";
        List<Map<String, Object>> difficultyDistribution = jdbcTemplate.queryForList(difficultySql);
        statistics.put("questionDifficultyDistribution", difficultyDistribution);

        return statistics;
    }

    @Override
    public Map<String, Object> getExamPaperStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        // 获取试卷总数
        int totalPapers = examPaperRepository.count();
        statistics.put("totalPapers", totalPapers);

        Double avgTotalScore = jdbcTemplate.queryForObject("SELECT IFNULL(AVG(total_score), 0) FROM exam_paper", Double.class);
        statistics.put("averageTotalScore", avgTotalScore != null ? avgTotalScore : 0.0);

        String paperTypeSql = "SELECT paper_type AS type, COUNT(*) AS count FROM exam_paper GROUP BY paper_type";
        List<Map<String, Object>> paperTypeDistribution = jdbcTemplate.queryForList(paperTypeSql);
        statistics.put("paperTypeDistribution", paperTypeDistribution);

        String papersByCourseSql = "SELECT c.course_name AS courseName, COUNT(p.paper_id) AS paperCount " +
                "FROM course c LEFT JOIN exam_paper p ON p.course_id = c.course_id " +
                "GROUP BY c.course_id, c.course_name " +
                "ORDER BY paperCount DESC";
        List<Map<String, Object>> papersByCourse = jdbcTemplate.queryForList(papersByCourseSql);
        statistics.put("papersByCourse", papersByCourse);
        return statistics;
    }
}
