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
        String sql = "SELECT q.question_id as questionId, q.question_type_id as questionType, q.question_content as content, q.kp_id as pointId, q.question_difficulty as difficulty, q.question_score as score, CONCAT('[{\"content\":\"', COALESCE(q.option_a, ''), '\