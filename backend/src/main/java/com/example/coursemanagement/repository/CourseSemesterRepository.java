package com.example.coursemanagement.repository;

import com.example.coursemanagement.entity.CourseSemester;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 课程学期关联数据访问层
 */
@Repository
public class CourseSemesterRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 新增课程学期关联
     */
    public int save(CourseSemester courseSemester) {
        String sql = "INSERT INTO course_semester (course_id, semester_id, create_time, update_time) VALUES (?, ?, NOW(), NOW())";
        return jdbcTemplate.update(sql, courseSemester.getCourseId(), courseSemester.getSemesterId());
    }

    /**
     * 删除课程的所有学期关联
     */
    public int deleteByCourseId(Integer courseId) {
        String sql = "DELETE FROM course_semester WHERE course_id = ?";
        return jdbcTemplate.update(sql, courseId);
    }

    /**
     * 删除学期的所有课程关联
     */
    public int deleteBySemesterId(Integer semesterId) {
        String sql = "DELETE FROM course_semester WHERE semester_id = ?";
        return jdbcTemplate.update(sql, semesterId);
    }

    /**
     * 根据课程ID查询关联的学期ID列表
     */
    public List<Integer> findSemesterIdsByCourseId(Integer courseId) {
        String sql = "SELECT semester_id FROM course_semester WHERE course_id = ?";
        return jdbcTemplate.queryForList(sql, Integer.class, courseId);
    }

    /**
     * 根据学期ID查询关联的课程ID列表
     */
    public List<Integer> findCourseIdsBySemesterId(Integer semesterId) {
        String sql = "SELECT course_id FROM course_semester WHERE semester_id = ?";
        return jdbcTemplate.queryForList(sql, Integer.class, semesterId);
    }

    /**
     * 批量保存课程学期关联
     */
    public void batchSave(Integer courseId, List<Integer> semesterIds) {
        // 先删除原有关联
        deleteByCourseId(courseId);
        // 批量插入新关联
        if (semesterIds != null && !semesterIds.isEmpty()) {
            String sql = "INSERT INTO course_semester (course_id, semester_id, create_time, update_time) VALUES (?, ?, NOW(), NOW())";
            jdbcTemplate.batchUpdate(sql, semesterIds, semesterIds.size(), (ps, semesterId) -> {
                ps.setInt(1, courseId);
                ps.setInt(2, semesterId);
            });
        }
    }
}