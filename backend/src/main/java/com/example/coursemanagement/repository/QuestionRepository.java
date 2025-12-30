package com.example.coursemanagement.repository;

import com.example.coursemanagement.entity.Question;
import com.example.coursemanagement.entity.QuestionOption;
import com.example.coursemanagement.entity.QuestionStatistics;
import com.example.coursemanagement.entity.QuestionTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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
        String sql = "SELECT q.question_id, q.question_type, q.question_content, q.kp_id, q.difficulty, q.score, q.correct_answer, q.analysis, q.is_used, q.create_time, q.update_time, kp.kp_name, kp.course_id, c.course_name " +
                "FROM question q " +
                "JOIN knowledge_point kp ON q.kp_id = kp.kp_id " +
                "JOIN course c ON kp.course_id = c.course_id";
        List<Question> questions = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Question.class));
        questions.forEach(this::loadQuestionOptions);
        questions.forEach(this::loadQuestionTags);
        questions.forEach(this::loadQuestionStatistics);
        return questions;
    }

    /**
     * 根据ID查询题目
     */
    public Optional<Question> findById(Integer id) {
        String sql = "SELECT q.question_id, q.question_type, q.question_content, q.kp_id, q.difficulty, q.score, q.correct_answer, q.analysis, q.is_used, q.create_time, q.update_time, kp.kp_name, kp.course_id, c.course_name " +
                "FROM question q " +
                "JOIN knowledge_point kp ON q.kp_id = kp.kp_id " +
                "JOIN course c ON kp.course_id = c.course_id " +
                "WHERE q.question_id = ?";
        List<Question> questions = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Question.class), id);
        if (questions.isEmpty()) {
            return Optional.empty();
        }
        Question question = questions.get(0);
        loadQuestionOptions(question);
        loadQuestionTags(question);
        loadQuestionStatistics(question);
        return Optional.of(question);
    }

    /**
     * 根据知识点ID查询题目
     */
    public List<Question> findByKpId(Integer kpId) {
        String sql = "SELECT q.question_id, q.question_type, q.question_content, q.kp_id, q.difficulty, q.score, q.correct_answer, q.analysis, q.is_used, q.create_time, q.update_time, kp.kp_name, kp.course_id, c.course_name " +
                "FROM question q " +
                "JOIN knowledge_point kp ON q.kp_id = kp.kp_id " +
                "JOIN course c ON kp.course_id = c.course_id " +
                "WHERE q.kp_id = ?";
        List<Question> questions = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Question.class), kpId);
        questions.forEach(this::loadQuestionOptions);
        questions.forEach(this::loadQuestionTags);
        questions.forEach(this::loadQuestionStatistics);
        return questions;
    }

    /**
     * 新增题目
     */
    @Transactional
    public int save(Question question) {
        String sql = "INSERT INTO question (question_type, question_content, kp_id, difficulty, score, correct_answer, analysis, category_id, status, creator_id, is_used, create_time, update_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";
        jdbcTemplate.update(sql, 
                question.getQuestionType(), 
                question.getQuestionContent(), 
                question.getKpId(), 
                question.getDifficulty(), 
                question.getScore(),
                question.getCorrectAnswer(),
                question.getAnalysis(),
                question.getCategoryId(),
                question.getStatus(),
                question.getCreatorId(),
                0); // 设置is_used的默认值为0
        
        // 获取新插入的题目ID
        Integer questionId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
        question.setQuestionId(questionId);
        
        // 保存题目选项
        if (question.getOptions() != null && !question.getOptions().isEmpty()) {
            saveQuestionOptions(question);
        }
        
        // 保存题目标签
        if (question.getTags() != null && !question.getTags().isEmpty()) {
            saveQuestionTags(question);
        }
        
        // 创建题目统计记录
        createQuestionStatistics(questionId);
        
        return questionId;
    }

    /**
     * 更新题目
     */
    @Transactional
    public int update(Question question) {
        String sql = "UPDATE question SET question_type = ?, question_content = ?, kp_id = ?, difficulty = ?, score = ?, correct_answer = ?, analysis = ?, category_id = ?, status = ?, update_time = NOW() WHERE question_id = ?";
        int result = jdbcTemplate.update(sql, 
                question.getQuestionType(), 
                question.getQuestionContent(), 
                question.getKpId(), 
                question.getDifficulty(), 
                question.getScore(),
                question.getCorrectAnswer(),
                question.getAnalysis(),
                question.getCategoryId(),
                question.getStatus(),
                question.getQuestionId());
        
        // 更新题目选项
        if (question.getOptions() != null) {
            // 先删除旧选项
            jdbcTemplate.update("DELETE FROM question_option WHERE question_id = ?", question.getQuestionId());
            // 再插入新选项
            saveQuestionOptions(question);
        }
        
        // 更新题目标签
        if (question.getTags() != null) {
            // 先删除旧标签关联
            jdbcTemplate.update("DELETE FROM question_tag_relation WHERE question_id = ?", question.getQuestionId());
            // 再插入新标签关联
            saveQuestionTags(question);
        }
        
        return result;
    }

    /**
     * 删除题目
     */
    @Transactional
    public int deleteById(Integer id) {
        // 删除题目标签关联
        jdbcTemplate.update("DELETE FROM question_tag_relation WHERE question_id = ?", id);
        // 删除题目选项
        jdbcTemplate.update("DELETE FROM question_option WHERE question_id = ?", id);
        // 删除题目统计
        jdbcTemplate.update("DELETE FROM question_statistics WHERE question_id = ?", id);
        // 删除题目
        return jdbcTemplate.update("DELETE FROM question WHERE question_id = ?", id);
    }

    /**
     * 批量删除题目
     */
    @Transactional
    public int deleteBatch(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        
        String placeholders = ids.stream().map(id -> "?").reduce((a, b) -> a + "," + b).orElse("");
        
        // 批量删除题目标签关联
        jdbcTemplate.update("DELETE FROM question_tag_relation WHERE question_id IN (" + placeholders + ")", ids.toArray());
        // 批量删除题目选项
        jdbcTemplate.update("DELETE FROM question_option WHERE question_id IN (" + placeholders + ")", ids.toArray());
        // 批量删除题目统计
        jdbcTemplate.update("DELETE FROM question_statistics WHERE question_id IN (" + placeholders + ")", ids.toArray());
        // 批量删除题目
        return jdbcTemplate.update("DELETE FROM question WHERE question_id IN (" + placeholders + ")", ids.toArray());
    }

    /**
     * 分页查询题目
     */
    public List<Question> findByPage(int page, int limit) {
        int offset = (page - 1) * limit;
        String sql = "SELECT q.question_id, q.question_type, q.question_content, q.kp_id, q.difficulty, q.score, q.correct_answer, q.analysis, q.is_used, q.create_time, q.update_time, kp.kp_name, kp.course_id, c.course_name " +
                "FROM question q " +
                "JOIN knowledge_point kp ON q.kp_id = kp.kp_id " +
                "JOIN course c ON kp.course_id = c.course_id " +
                "LIMIT ? OFFSET ?";
        List<Question> questions = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Question.class), limit, offset);
        questions.forEach(this::loadQuestionOptions);
        questions.forEach(this::loadQuestionTags);
        questions.forEach(this::loadQuestionStatistics);
        return questions;
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
        String sql = "SELECT q.question_id, q.question_type, q.question_content, q.kp_id, q.difficulty, q.score, q.correct_answer, q.analysis, q.is_used, q.create_time, q.update_time, kp.kp_name, kp.course_id, c.course_name " +
                "FROM question q " +
                "JOIN knowledge_point kp ON q.kp_id = kp.kp_id " +
                "JOIN course c ON kp.course_id = c.course_id " +
                "WHERE kp.course_id = ?";
        List<Question> questions = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Question.class), courseId);
        questions.forEach(this::loadQuestionOptions);
        questions.forEach(this::loadQuestionTags);
        questions.forEach(this::loadQuestionStatistics);
        return questions;
    }

    /**
     * 根据题目类型查询题目
     */
    public List<Question> findByType(Integer type) {
        String sql = "SELECT q.question_id, q.question_type, q.question_content, q.kp_id, q.difficulty, q.score, q.correct_answer, q.analysis, q.is_used, q.create_time, q.update_time, kp.kp_name, kp.course_id, c.course_name " +
                "FROM question q " +
                "JOIN knowledge_point kp ON q.kp_id = kp.kp_id " +
                "JOIN course c ON kp.course_id = c.course_id " +
                "WHERE q.question_type = ?";
        List<Question> questions = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Question.class), type);
        questions.forEach(this::loadQuestionOptions);
        questions.forEach(this::loadQuestionTags);
        questions.forEach(this::loadQuestionStatistics);
        return questions;
    }

    /**
     * 根据难度查询题目
     */
    public List<Question> findByDifficulty(String difficulty) {
        String sql = "SELECT q.question_id, q.question_type, q.question_content, q.kp_id, q.difficulty, q.score, q.correct_answer, q.analysis, q.is_used, q.create_time, q.update_time, kp.kp_name, kp.course_id, c.course_name " +
                "FROM question q " +
                "JOIN knowledge_point kp ON q.kp_id = kp.kp_id " +
                "JOIN course c ON kp.course_id = c.course_id " +
                "WHERE q.difficulty = ?";
        List<Question> questions = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Question.class), difficulty);
        questions.forEach(this::loadQuestionOptions);
        questions.forEach(this::loadQuestionTags);
        questions.forEach(this::loadQuestionStatistics);
        return questions;
    }
    
    /**
     * 根据分类ID查询题目
     */
    public List<Question> findByCategoryId(Integer categoryId) {
        String sql = "SELECT q.question_id, q.question_type, q.question_content, q.kp_id, q.difficulty, q.score, q.correct_answer, q.analysis, q.is_used, q.create_time, q.update_time, kp.kp_name, kp.course_id, c.course_name " +
                "FROM question q " +
                "JOIN knowledge_point kp ON q.kp_id = kp.kp_id " +
                "JOIN course c ON kp.course_id = c.course_id " +
                "WHERE q.category_id = ?";
        List<Question> questions = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Question.class), categoryId);
        questions.forEach(this::loadQuestionOptions);
        questions.forEach(this::loadQuestionTags);
        questions.forEach(this::loadQuestionStatistics);
        return questions;
    }
    
    /**
     * 更新题目使用状态
     */
    public int updateUsedStatus(String sql, Integer isUsed, Integer questionId) {
        return jdbcTemplate.update(sql, isUsed, questionId);
    }

    /**
     * 带条件的分页查询题目
     */
    public List<Question> findByPage(int page, int limit, Integer questionType, String difficulty, Integer categoryId) {
        int offset = (page - 1) * limit;
        StringBuilder sql = new StringBuilder("SELECT q.question_id, q.question_type, q.question_content, q.kp_id, q.difficulty, q.score, q.correct_answer, q.analysis, q.is_used, q.create_time, q.update_time, kp.kp_name, kp.course_id, c.course_name " +
                "FROM question q " +
                "JOIN knowledge_point kp ON q.kp_id = kp.kp_id " +
                "JOIN course c ON kp.course_id = c.course_id " +
                "WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        if (questionType != null) {
            sql.append(" AND q.question_type = ?");
            params.add(questionType);
        }
        
        if (difficulty != null && !difficulty.isEmpty()) {
            sql.append(" AND q.difficulty = ?");
            params.add(difficulty);
        }
        
        if (categoryId != null) {
            sql.append(" AND q.category_id = ?");
            params.add(categoryId);
        }
        
        sql.append(" LIMIT ? OFFSET ?");
        params.add(limit);
        params.add(offset);
        
        List<Question> questions = jdbcTemplate.query(sql.toString(), new BeanPropertyRowMapper<>(Question.class), params.toArray());
        questions.forEach(this::loadQuestionOptions);
        questions.forEach(this::loadQuestionTags);
        questions.forEach(this::loadQuestionStatistics);
        return questions;
    }

    /**
     * 带条件的题目计数
     */
    public int count(Integer questionType, String difficulty, Integer categoryId) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM question q WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        if (questionType != null) {
            sql.append(" AND q.question_type = ?");
            params.add(questionType);
        }
        
        if (difficulty != null && !difficulty.isEmpty()) {
            sql.append(" AND q.difficulty = ?");
            params.add(difficulty);
        }
        
        if (categoryId != null) {
            sql.append(" AND q.category_id = ?");
            params.add(categoryId);
        }
        
        return jdbcTemplate.queryForObject(sql.toString(), Integer.class, params.toArray());
    }
    
    /**
     * 加载题目选项
     */
    private void loadQuestionOptions(Question question) {
        String sql = "SELECT option_id, question_id, option_text, option_order, is_correct, create_time, update_time FROM question_option WHERE question_id = ? ORDER BY option_order";
        List<QuestionOption> options = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(QuestionOption.class), question.getQuestionId());
        question.setOptions(options);
    }
    
    /**
     * 保存题目选项
     */
    private void saveQuestionOptions(Question question) {
        String sql = "INSERT INTO question_option (question_id, option_text, option_order, is_correct) VALUES (?, ?, ?, ?)";
        for (QuestionOption option : question.getOptions()) {
            jdbcTemplate.update(sql, question.getQuestionId(), option.getOptionText(), option.getOptionOrder(), option.getIsCorrect());
        }
    }
    
    /**
     * 加载题目标签
     */
    private void loadQuestionTags(Question question) {
        String sql = "SELECT t.tag_id, t.tag_name, t.tag_description, t.is_active, t.create_time, t.update_time FROM question_tag t JOIN question_tag_relation r ON t.tag_id = r.tag_id WHERE r.question_id = ?";
        List<QuestionTag> tags = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(QuestionTag.class), question.getQuestionId());
        question.setTags(tags);
    }
    
    /**
     * 保存题目标签
     */
    private void saveQuestionTags(Question question) {
        String sql = "INSERT INTO question_tag_relation (question_id, tag_id) VALUES (?, ?)";
        for (QuestionTag tag : question.getTags()) {
            jdbcTemplate.update(sql, question.getQuestionId(), tag.getTagId());
        }
    }
    
    /**
     * 加载题目统计
     */
    private void loadQuestionStatistics(Question question) {
        String sql = "SELECT stat_id, question_id, usage_count, correct_count, incorrect_count, correct_rate, last_used_time, create_time, update_time FROM question_statistics WHERE question_id = ?";
        List<QuestionStatistics> stats = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(QuestionStatistics.class), question.getQuestionId());
        if (!stats.isEmpty()) {
            question.setStatistics(stats.get(0));
        }
    }
    
    /**
     * 创建题目统计
     */
    private void createQuestionStatistics(Integer questionId) {
        String sql = "INSERT INTO question_statistics (question_id, usage_count, correct_count, incorrect_count, correct_rate) VALUES (?, 0, 0, 0, 0)";
        jdbcTemplate.update(sql, questionId);
    }
}
