package com.example.coursemanagement.repository;

import com.example.coursemanagement.entity.QuestionTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 题目标签数据访问层
 */
@Repository
public class QuestionTagRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 查询所有题目标签
     */
    public List<QuestionTag> findAll() {
        String sql = "SELECT tag_id, tag_name, tag_description, is_active, create_time, update_time FROM question_tag WHERE is_active = 1 ORDER BY tag_name";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(QuestionTag.class));
    }

    /**
     * 根据ID查询题目标签
     */
    public Optional<QuestionTag> findById(Integer id) {
        String sql = "SELECT tag_id, tag_name, tag_description, is_active, create_time, update_time FROM question_tag WHERE tag_id = ?";
        List<QuestionTag> tags = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(QuestionTag.class), id);
        return tags.isEmpty() ? Optional.empty() : Optional.of(tags.get(0));
    }

    /**
     * 根据名称查询题目标签
     */
    public Optional<QuestionTag> findByName(String name) {
        String sql = "SELECT tag_id, tag_name, tag_description, is_active, create_time, update_time FROM question_tag WHERE tag_name = ?";
        List<QuestionTag> tags = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(QuestionTag.class), name);
        return tags.isEmpty() ? Optional.empty() : Optional.of(tags.get(0));
    }

    /**
     * 新增题目标签
     */
    public int save(QuestionTag tag) {
        String sql = "INSERT INTO question_tag (tag_name, tag_description, is_active) VALUES (?, ?, ?)";
        return jdbcTemplate.update(sql, 
                tag.getTagName(), 
                tag.getTagDescription(), 
                tag.getIsActive());
    }

    /**
     * 更新题目标签
     */
    public int update(QuestionTag tag) {
        String sql = "UPDATE question_tag SET tag_name = ?, tag_description = ?, is_active = ? WHERE tag_id = ?";
        return jdbcTemplate.update(sql, 
                tag.getTagName(), 
                tag.getTagDescription(), 
                tag.getIsActive(), 
                tag.getTagId());
    }

    /**
     * 删除题目标签（软删除）
     */
    public int deleteById(Integer id) {
        String sql = "UPDATE question_tag SET is_active = 0 WHERE tag_id = ?";
        return jdbcTemplate.update(sql, id);
    }
}