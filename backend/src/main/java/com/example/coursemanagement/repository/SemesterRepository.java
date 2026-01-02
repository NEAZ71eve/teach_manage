package com.example.coursemanagement.repository;

import com.example.coursemanagement.entity.Semester;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 学期数据访问层
 */
@Repository
public class SemesterRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 查询所有学期
     */
    public List<Semester> findAll() {
        String sql = "SELECT * FROM semester ORDER BY semester_id DESC";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Semester.class));
    }

    /**
     * 根据ID查询学期
     */
    public Optional<Semester> findById(Integer id) {
        String sql = "SELECT * FROM semester WHERE semester_id = ?";
        List<Semester> semesters = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Semester.class), id);
        return semesters.isEmpty() ? Optional.empty() : Optional.of(semesters.get(0));
    }

    /**
     * 新增学期
     */
    public int save(Semester semester) {
        String sql = "INSERT INTO semester (semester_name, semester_code, start_date, end_date, status, create_time, update_time) VALUES (?, ?, ?, ?, ?, NOW(), NOW())";
        return jdbcTemplate.update(sql, 
                semester.getSemesterName(), 
                semester.getSemesterCode(), 
                semester.getStartDate() != null ? java.sql.Date.valueOf(java.time.LocalDate.parse(semester.getStartDate())) : null,
                semester.getEndDate() != null ? java.sql.Date.valueOf(java.time.LocalDate.parse(semester.getEndDate())) : null,
                semester.getStatus());
    }

    /**
     * 更新学期
     */
    public int update(Semester semester) {
        String sql = "UPDATE semester SET semester_name = ?, semester_code = ?, start_date = ?, end_date = ?, status = ?, update_time = NOW() WHERE semester_id = ?";
        return jdbcTemplate.update(sql, 
                semester.getSemesterName(), 
                semester.getSemesterCode(), 
                semester.getStartDate() != null ? java.sql.Date.valueOf(java.time.LocalDate.parse(semester.getStartDate())) : null,
                semester.getEndDate() != null ? java.sql.Date.valueOf(java.time.LocalDate.parse(semester.getEndDate())) : null,
                semester.getStatus(),
                semester.getSemesterId());
    }

    /**
     * 删除学期
     */
    public int deleteById(Integer id) {
        String sql = "DELETE FROM semester WHERE semester_id = ?";
        return jdbcTemplate.update(sql, id);
    }

    /**
     * 查询学期总数
     */
    public int count() {
        String sql = "SELECT COUNT(*) FROM semester";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }
}