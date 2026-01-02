package com.example.coursemanagement.repository;

import com.example.coursemanagement.entity.ExamPaperQuestion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 试卷题目关联数据访问层，使用Spring JDBC Template实现
 */
@Repository
public class ExamPaperQuestionRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 根据试卷ID获取题目列表
     */
    public List<ExamPaperQuestion> findByPaperId(String paperId) {
        String sql = "SELECT paper_question_id as detailId, paper_id as paperId, question_id as questionId, question_order as questionOrder FROM exam_paper_question WHERE paper_id = ?";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(ExamPaperQuestion.class), paperId);
    }

    /**
     * 根据题目ID获取试卷题目关联列表
     */
    public List<ExamPaperQuestion> findByQuestionId(Integer questionId) {
        String sql = "SELECT paper_question_id as detailId, paper_id as paperId, question_id as questionId, question_order as questionOrder FROM exam_paper_question WHERE question_id = ?";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(ExamPaperQuestion.class), questionId);
    }
    
    /**
     * 根据题目ID获取试卷题目关联列表（String类型参数）
     */
    public List<ExamPaperQuestion> findByQuestionId(String questionId) {
        return findByQuestionId(Integer.valueOf(questionId));
    }

    /**
     * 新增试卷题目关联
     */
    public int save(ExamPaperQuestion examPaperQuestion) {
        String sql = "INSERT INTO exam_paper_question (paper_id, question_id, question_order) VALUES (?, ?, ?)";
        return jdbcTemplate.update(sql, 
                Integer.parseInt(examPaperQuestion.getPaperId()), 
                Integer.parseInt(examPaperQuestion.getQuestionId()),
                examPaperQuestion.getQuestionOrder());
    }

    /**
     * 更新试卷题目关联
     */
    public int update(ExamPaperQuestion examPaperQuestion) {
        String sql = "UPDATE exam_paper_question SET question_id = ?, question_order = ? WHERE paper_question_id = ?";
        return jdbcTemplate.update(sql, 
                examPaperQuestion.getQuestionId(), 
                examPaperQuestion.getQuestionOrder(), 
                examPaperQuestion.getDetailId());
    }

    /**
     * 根据ID删除试卷题目关联
     */
    public int deleteById(Integer id) {
        String sql = "DELETE FROM exam_paper_question WHERE paper_question_id = ?";
        return jdbcTemplate.update(sql, id);
    }

    /**
     * 根据试卷ID删除所有关联题目
     */
    public int deleteByPaperId(String paperId) {
        String sql = "DELETE FROM exam_paper_question WHERE paper_id = ?";
        return jdbcTemplate.update(sql, paperId);
    }

    /**
     * 根据试卷ID和题目ID删除关联
     */
    public int deleteByPaperIdAndQuestionId(String paperId, String questionId) {
        String sql = "DELETE FROM exam_paper_question WHERE paper_id = ? AND question_id = ?";
        return jdbcTemplate.update(sql, paperId, questionId);
    }
}
