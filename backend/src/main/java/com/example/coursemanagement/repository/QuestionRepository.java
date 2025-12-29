package com.example.coursemanagement.repository;

import com.example.coursemanagement.entity.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
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
        String sql = "SELECT q.question_id as questionId, q.question_type_id as questionType, q.question_content as content, q.kp_id as pointId, q.question_difficulty as difficulty, q.question_score as score FROM question q";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Question.class));
    }

    /**
     * 根据ID查询题目
     */
    public Optional<Question> findById(Integer id) {
        String sql = "SELECT q.question_id as questionId, q.question_type_id as questionType, q.question_content as content, q.kp_id as pointId, q.question_difficulty as difficulty, q.question_score as score FROM question q WHERE q.question_id = ?";
        List<Question> questions = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Question.class), id);
        return questions.isEmpty() ? Optional.empty() : Optional.of(questions.get(0));
    }

    /**
     * 根据知识点ID查询题目
     */
    public List<Question> findByPointId(Integer pointId) {
        String sql = "SELECT q.question_id as questionId, q.question_type_id as questionType, q.question_content as content, q.kp_id as pointId, q.question_difficulty as difficulty, q.question_score as score FROM question q WHERE q.kp_id = ?";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Question.class), pointId);
    }

    /**
     * 新增题目
     */
    public int save(Question question) {
        String sql = "INSERT INTO question (question_type_id, question_content, kp_id, question_difficulty, question_score, option_a, option_b, option_c, option_d, correct_answer) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql, 
                question.getQuestionType(), 
                question.getContent(), 
                question.getPointId(), 
                question.getDifficulty(), 
                question.getScore(),
                "", "", "", "", "");
    }

    /**
     * 更新题目
     */
    public int update(Question question) {
        String sql = "UPDATE question SET question_type_id = ?, question_content = ?, kp_id = ?, question_difficulty = ?, question_score = ? WHERE question_id = ?";
        return jdbcTemplate.update(sql, 
                question.getQuestionType(), 
                question.getContent(), 
                question.getPointId(), 
                question.getDifficulty(), 
                question.getScore(),
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
        String sql = "SELECT q.question_id as questionId, q.question_type_id as questionType, q.question_content as content, q.kp_id as pointId, q.question_difficulty as difficulty, q.question_score as score FROM question q LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Question.class), limit, offset);
    }

    /**
     * 查询题目总数
     */
    public int count() {
        String sql = "SELECT COUNT(*) FROM question";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    /**
     * 根据课程ID查询题目
     */
    public List<Question> findByCourseId(Integer courseId) {
        // 假设题目和课程通过知识点关联，先根据课程ID查询知识点，再查询题目
        String sql = "SELECT q.question_id as questionId, q.question_type_id as questionType, q.question_content as content, q.kp_id as pointId, q.question_difficulty as difficulty, q.question_score as score " +
                "FROM question q " +
                "JOIN knowledge_point kp ON q.kp_id = kp.kp_id " +
                "WHERE kp.course_id = ?";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Question.class), courseId);
    }

    /**
     * 根据题目类型查询题目
     */
    public List<Question> findByType(String type) {
        String sql = "SELECT q.question_id as questionId, q.question_type_id as questionType, q.question_content as content, q.kp_id as pointId, q.question_difficulty as difficulty, q.question_score as score " +
                "FROM question q " +
                "WHERE q.question_type_id = ?";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Question.class), type);
    }

    /**
     * 根据难度查询题目
     */
    public List<Question> findByDifficulty(String difficulty) {
        String sql = "SELECT q.question_id as questionId, q.question_type_id as questionType, q.question_content as content, q.kp_id as pointId, q.question_difficulty as difficulty, q.question_score as score " +
                "FROM question q " +
                "WHERE q.question_difficulty = ?";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Question.class), difficulty);
    }

    /**
     * 带条件的分页查询题目
     */
    public List<Question> findByPage(int page, int limit, String questionType, String difficulty) {
        int offset = (page - 1) * limit;
        StringBuilder sql = new StringBuilder("SELECT q.question_id as questionId, q.question_type_id as questionType, q.question_content as content, q.kp_id as pointId, q.question_difficulty as difficulty, q.question_score as score " +
                "FROM question q WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        if (questionType != null && !questionType.isEmpty()) {
            sql.append(" AND q.question_type_id = ?");
            params.add(questionType);
        }
        
        if (difficulty != null && !difficulty.isEmpty()) {
            sql.append(" AND q.question_difficulty = ?");
            params.add(difficulty);
        }
        
        sql.append(" LIMIT ? OFFSET ?");
        params.add(limit);
        params.add(offset);
        
        return jdbcTemplate.query(sql.toString(), new BeanPropertyRowMapper<>(Question.class), params.toArray());
    }

    /**
     * 带条件的题目计数
     */
    public int count(String questionType, String difficulty) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM question q WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        if (questionType != null && !questionType.isEmpty()) {
            sql.append(" AND q.question_type_id = ?");
            params.add(questionType);
        }
        
        if (difficulty != null && !difficulty.isEmpty()) {
            sql.append(" AND q.question_difficulty = ?");
            params.add(difficulty);
        }
        
        return jdbcTemplate.queryForObject(sql.toString(), Integer.class, params.toArray());
    }
}