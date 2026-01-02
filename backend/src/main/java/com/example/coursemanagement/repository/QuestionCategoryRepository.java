package com.example.coursemanagement.repository;

import com.example.coursemanagement.entity.QuestionCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 题目分类数据访问层
 */
@Repository
public class QuestionCategoryRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 查询所有题目分类
     */
    public List<QuestionCategory> findAll() {
        String sql = "SELECT category_id, category_name, description, parent_category_id, category_order, is_active, create_time, update_time FROM question_category WHERE is_active = 1 ORDER BY category_order";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(QuestionCategory.class));
    }

    /**
     * 根据ID查询题目分类
     */
    public Optional<QuestionCategory> findById(Integer id) {
        String sql = "SELECT category_id, category_name, description, parent_category_id, category_order, is_active, create_time, update_time FROM question_category WHERE category_id = ?";
        List<QuestionCategory> categories = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(QuestionCategory.class), id);
        return categories.isEmpty() ? Optional.empty() : Optional.of(categories.get(0));
    }

    /**
     * 新增题目分类
     */
    public int save(QuestionCategory category) {
        String sql = "INSERT INTO question_category (category_name, description, parent_category_id, category_order, is_active) VALUES (?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql, 
                category.getCategoryName(), 
                category.getDescription(), 
                category.getParentCategoryId(), 
                category.getCategoryOrder(), 
                category.getIsActive());
    }

    /**
     * 更新题目分类
     */
    public int update(QuestionCategory category) {
        String sql = "UPDATE question_category SET category_name = ?, description = ?, parent_category_id = ?, category_order = ?, is_active = ? WHERE category_id = ?";
        return jdbcTemplate.update(sql, 
                category.getCategoryName(), 
                category.getDescription(), 
                category.getParentCategoryId(), 
                category.getCategoryOrder(), 
                category.getIsActive(), 
                category.getCategoryId());
    }

    /**
     * 删除题目分类（软删除）
     */
    public int deleteById(Integer id) {
        String sql = "UPDATE question_category SET is_active = 0 WHERE category_id = ?";
        return jdbcTemplate.update(sql, id);
    }

    /**
     * 查询子分类
     */
    public List<QuestionCategory> findByParentId(Integer parentId) {
        String sql = "SELECT category_id, category_name, description, parent_category_id, category_order, is_active, create_time, update_time FROM question_category WHERE parent_category_id = ? AND is_active = 1 ORDER BY category_order";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(QuestionCategory.class), parentId);
    }
}