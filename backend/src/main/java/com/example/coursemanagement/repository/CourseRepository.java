package com.example.coursemanagement.repository;

import com.example.coursemanagement.entity.Course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 课程数据访问层，使用Spring JDBC Template实现
 */
@Repository
public class CourseRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 查询所有课程
     */
    public List<Course> findAll() {
        String sql = "SELECT * FROM course";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Course.class));
    }

    /**
     * 根据ID查询课程
     */
    public Optional<Course> findById(Integer id) {
        String sql = "SELECT * FROM course WHERE course_id = ?";
        List<Course> courses = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Course.class), id);
        return courses.isEmpty() ? Optional.empty() : Optional.of(courses.get(0));
    }

    /**
     * 新增课程
     */
    public int save(Course course) {
        String sql = "INSERT INTO course (course_name, course_code, major_id, program_id, credit, total_hours, theoretical_hours, practical_hours, course_type, course_nature, exam_mark, course_category, description, create_time, update_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";
        return jdbcTemplate.update(sql, 
                course.getCourseName(), 
                course.getCourseCode(), 
                course.getMajorId(), 
                course.getProgramId(), 
                course.getCredit(), 
                course.getTotalHours(), 
                course.getTheoreticalHours(), 
                course.getPracticalHours(), 
                course.getCourseType(), 
                course.getCourseNature(), 
                course.getExamMark(), 
                course.getCourseCategory(), 
                course.getDescription());
    }

    /**
     * 更新课程
     */
    public int update(Course course) {
        String sql = "UPDATE course SET course_name = ?, course_code = ?, major_id = ?, program_id = ?, credit = ?, total_hours = ?, theoretical_hours = ?, practical_hours = ?, course_type = ?, course_nature = ?, exam_mark = ?, course_category = ?, description = ?, update_time = NOW() WHERE course_id = ?";
        return jdbcTemplate.update(sql, 
                course.getCourseName(), 
                course.getCourseCode(), 
                course.getMajorId(), 
                course.getProgramId(), 
                course.getCredit(), 
                course.getTotalHours(), 
                course.getTheoreticalHours(), 
                course.getPracticalHours(), 
                course.getCourseType(), 
                course.getCourseNature(), 
                course.getExamMark(), 
                course.getCourseCategory(), 
                course.getDescription(), 
                course.getCourseId());
    }

    /**
     * 删除课程
     */
    public int deleteById(Integer id) {
        String sql = "DELETE FROM course WHERE course_id = ?";
        return jdbcTemplate.update(sql, id);
    }

    /**
     * 批量删除课程
     */
    public int deleteBatch(List<Integer> ids) {
        String sql = "DELETE FROM course WHERE course_id IN (" + ids.stream().map(id -> "?").reduce((a, b) -> a + "," + b).orElse("") + ")";
        return jdbcTemplate.update(sql, ids.toArray());
    }

    /**
     * 搜索课程
     */
    public List<Course> search(String keyword) {
        String sql = "SELECT * FROM course WHERE course_name LIKE ? OR course_code LIKE ?";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Course.class), "%" + keyword + "%", "%" + keyword + "%");
    }

    /**
     * 分页查询课程
     */
    public List<Course> findByPage(int page, int limit) {
        int offset = (page - 1) * limit;
        String sql = "SELECT * FROM course LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Course.class), limit, offset);
    }

    /**
     * 查询课程总数
     */
    public int count() {
        String sql = "SELECT COUNT(*) FROM course";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }
}
