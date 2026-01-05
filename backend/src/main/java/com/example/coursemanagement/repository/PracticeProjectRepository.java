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
        String sql = "SELECT * FROM practice_project WHERE id = ?";
        List<PracticeProject> projects = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(PracticeProject.class), id);
        return projects.isEmpty() ? Optional.empty() : Optional.of(projects.get(0));
    }

    /**
     * 插入新项目
     */
    public int save(PracticeProject project) {
        String sql = "INSERT INTO practice_project (course_code, project_name, semester, weeks, credit, remarks, teacher_id, create_time, update_time) VALUES (?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";
        return jdbcTemplate.update(sql,
                project.getCourseCode(),
                project.getProjectName(),
                project.getSemester(),
                project.getWeeks(),
                project.getCredit(),
                project.getRemarks(),
                project.getTeacherId());
    }

    /**
     * 更新项目
     */
    public int update(PracticeProject project) {
        String sql = "UPDATE practice_project SET course_code = ?, project_name = ?, semester = ?, weeks = ?, credit = ?, remarks = ?, teacher_id = ?, update_time = NOW() WHERE id = ?";
        return jdbcTemplate.update(sql,
                project.getCourseCode(),
                project.getProjectName(),
                project.getSemester(),
                project.getWeeks(),
                project.getCredit(),
                project.getRemarks(),
                project.getTeacherId(),
                project.getId());
    }

    /**
     * 删除项目
     */
    public int deleteById(Integer id) {
        String sql = "DELETE FROM practice_project WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }
}
