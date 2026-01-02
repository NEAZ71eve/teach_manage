package com.example.coursemanagement.repository;

import com.example.coursemanagement.entity.PracticeProject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 实践项目数据访问层，使用Spring JDBC Template实现
 */
@Repository
public class PracticeProjectRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 查询所有实践项目
     */
    public List<PracticeProject> findAll() {
        String sql = "SELECT * FROM practice_project ORDER BY create_time DESC";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(PracticeProject.class));
    }

    /**
     * 根据ID查询项目
     */
    public Optional<PracticeProject> findById(Integer id) {
        String sql = "SELECT * FROM practice_project WHERE project_id = ?";
        List<PracticeProject> projects = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(PracticeProject.class), id);
        return projects.isEmpty() ? Optional.empty() : Optional.of(projects.get(0));
    }

    /**
     * 插入新项目
     */
    public int save(PracticeProject project) {
        String sql = "INSERT INTO practice_project (title, description, publisher, create_time, deadline) VALUES (?, ?, ?, NOW(), ?)";
        return jdbcTemplate.update(sql, project.getTitle(), project.getDescription(), project.getPublisher(), project.getDeadline());
    }

    /**
     * 更新项目
     */
    public int update(PracticeProject project) {
        String sql = "UPDATE practice_project SET title=?, description=?, publisher=?, deadline=? WHERE project_id=?";
        return jdbcTemplate.update(sql, project.getTitle(), project.getDescription(), project.getPublisher(), project.getDeadline(), project.getProjectId());
    }

    /**
     * 删除项目
     */
    public int deleteById(Integer id) {
        String sql = "DELETE FROM practice_project WHERE project_id = ?";
        return jdbcTemplate.update(sql, id);
    }
}