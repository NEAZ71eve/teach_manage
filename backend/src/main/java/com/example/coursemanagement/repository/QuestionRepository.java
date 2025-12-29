package com.example.coursemanagement.repository;

import com.example.coursemanagement.entity.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 题目数据访问层，使用Spring JDBC Template实现
 */
@Repository
public class QuestionRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 查询所有题目
     */
    public List<Question> findAll() {
        String sql = "SELECT * FROM question";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Question.class));
    }

    /**
     * 根据ID查询题目
     */
    public Optional<Question> findById(Integer id) {
        String sql = "SELECT * FROM question WHERE question_id = ?";
        List<Question> questions = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Question.class), id);
        return questions.isEmpty() ? Optional.empty() : Optional.of(questions.get(0));
    }

    /**
     * 根据课程ID查询题目
     */
    public List<Question> findByCourseId(Integer courseId) {
        String sql = "SELECT * FROM question WHERE course_id = ?";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Question.class), courseId);
    }

    /**
     * 根据知识点ID查询题目
     */
    public List<Question> findByPointId(Integer pointId) {
        String sql = "SELECT * FROM question WHERE point_id = ?";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Question.class), pointId);
    }

    /**
     * 根据题型查询题目
     */
    public List<Question> findByType(String questionType) {
        String sql = "SELECT * FROM question WHERE question_type = ?";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Question.class), questionType);
    }

    /**
     * 根据难度查询题目
     */
    public List<Question> findByDifficulty(String difficulty) {
        String sql = "SELECT * FROM question WHERE difficulty = ?";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Question.class), difficulty);
    }

    /**
     * 新增题目
     */
    public int save(Question question) {
        String sql = "INSERT INTO question (course_id, point_id, question_type, content, options, answer, difficulty, score, teacher_id, create_time, update_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";
        return jdbcTemplate.update(sql, 
                question.getCourseId(), 
                question.getPointId(), 
                question.getQuestionType(), 
                question.getContent(), 
                question.getOptions(), 
                question.getAnswer(), 
                question.getDifficulty(), 
                question.getScore(), 
                question.getTeacherId());
    }

    /**
     * 更新题目
     */
    public int update(Question question) {
        String sql = "UPDATE question SET course_id = ?, point_id = ?, question_type = ?, content = ?, options = ?, answer = ?, difficulty = ?, score = ?, teacher_id = ?, update_time = NOW() WHERE question_id = ?";
        return jdbcTemplate.update(sql, 
                question.getCourseId(), 
                question.getPointId(), 
                question.getQuestionType(), 
                question.getContent(), 
                question.getOptions(), 
                question.getAnswer(), 
                question.getDifficulty(), 
                question.getScore(), 
                question.getTeacherId(), 
                question.getQuestionId());
    }

    /**
     * 删除题目
     */
    public int deleteById(Integer id) {
        String sql = "DELETE FROM question WHERE question_id = ?";
        return jdbcTemplate.update(sql, id);
    }

    /**
     * 批量删除题目
     */
    public int deleteBatch(List<Integer> ids) {
        String sql = "DELETE FROM question WHERE question_id IN (" + ids.stream().map(id -> "?").reduce((a, b) -> a + "," + b).orElse("") + ")";
        return jdbcTemplate.update(sql, ids.toArray());
    }

    /**
     * 分页查询题目
     */
    public List<Question> findByPage(int page, int limit) {
        int offset = (page - 1) * limit;
        String sql = "SELECT * FROM question LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Question.class), limit, offset);
    }

    /**
     * 查询题目总数
     */
    public int count() {
        String sql = "SELECT COUNT(*) FROM question";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }
}
