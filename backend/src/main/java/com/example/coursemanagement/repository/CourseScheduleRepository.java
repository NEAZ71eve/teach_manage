package com.example.coursemanagement.repository;

import com.example.coursemanagement.entity.CourseSchedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 课程表数据访问层，使用Spring JDBC Template实现
 */
@Repository
public class CourseScheduleRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 查询所有课程表
     */
    public List<CourseSchedule> findAll() {
        String sql = "SELECT * FROM course_schedule";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(CourseSchedule.class));
    }

    /**
     * 根据ID查询课程表
     */
    public Optional<CourseSchedule> findById(Integer id) {
        String sql = "SELECT * FROM course_schedule WHERE schedule_id = ?";
        List<CourseSchedule> schedules = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(CourseSchedule.class), id);
        return schedules.isEmpty() ? Optional.empty() : Optional.of(schedules.get(0));
    }

    /**
     * 根据学期ID查询课程表
     */
    public List<CourseSchedule> findBySemesterId(Integer semesterId) {
        String sql = "SELECT * FROM course_schedule WHERE semester_id = ?";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(CourseSchedule.class), semesterId);
    }

    /**
     * 根据学期ID和星期几查询课程表（周课表）
     */
    public List<CourseSchedule> findWeekSchedule(Integer semesterId, Integer weekDay) {
        String sql = "SELECT * FROM course_schedule WHERE semester_id = ? AND week_day = ? ORDER BY class_section";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(CourseSchedule.class), semesterId, weekDay);
    }

    /**
     * 根据课程ID查询课程表
     */
    public List<CourseSchedule> findByCourseId(Integer courseId) {
        String sql = "SELECT * FROM course_schedule WHERE course_id = ?";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(CourseSchedule.class), courseId);
    }

    /**
     * 新增课程表
     */
    public int save(CourseSchedule schedule) {
        String sql = "INSERT INTO course_schedule (course_id, semester_id, week_day, class_section, start_time, end_time, classroom, teacher, class_name, create_time, update_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";
        return jdbcTemplate.update(sql, 
                schedule.getCourseId(),
                schedule.getSemesterId(),
                schedule.getWeekDay(),
                schedule.getClassSection(),
                schedule.getStartTime(),
                schedule.getEndTime(),
                schedule.getClassroom(),
                schedule.getTeacher(),
                schedule.getClassName());
    }

    /**
     * 更新课程表
     */
    public int update(CourseSchedule schedule) {
        String sql = "UPDATE course_schedule SET course_id = ?, semester_id = ?, week_day = ?, class_section = ?, start_time = ?, end_time = ?, classroom = ?, teacher = ?, class_name = ?, update_time = NOW() WHERE schedule_id = ?";
        return jdbcTemplate.update(sql, 
                schedule.getCourseId(),
                schedule.getSemesterId(),
                schedule.getWeekDay(),
                schedule.getClassSection(),
                schedule.getStartTime(),
                schedule.getEndTime(),
                schedule.getClassroom(),
                schedule.getTeacher(),
                schedule.getClassName(),
                schedule.getScheduleId());
    }

    /**
     * 删除课程表
     */
    public int deleteById(Integer id) {
        String sql = "DELETE FROM course_schedule WHERE schedule_id = ?";
        return jdbcTemplate.update(sql, id);
    }

    /**
     * 批量删除课程表
     */
    public int deleteBatch(List<Integer> ids) {
        String sql = "DELETE FROM course_schedule WHERE schedule_id IN (" + ids.stream().map(id -> "?").reduce((a, b) -> a + "," + b).orElse("") + ")";
        return jdbcTemplate.update(sql, ids.toArray());
    }

    /**
     * 删除指定课程的所有课程表
     */
    public int deleteByCourseId(Integer courseId) {
        String sql = "DELETE FROM course_schedule WHERE course_id = ?";
        return jdbcTemplate.update(sql, courseId);
    }

    /**
     * 删除指定学期的所有课程表
     */
    public int deleteBySemesterId(Integer semesterId) {
        String sql = "DELETE FROM course_schedule WHERE semester_id = ?";
        return jdbcTemplate.update(sql, semesterId);
    }

    /**
     * 分页查询课程表
     */
    public List<CourseSchedule> findByPage(Integer page, Integer limit) {
        int offset = (page - 1) * limit;
        String sql = "SELECT * FROM course_schedule LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(CourseSchedule.class), limit, offset);
    }

    /**
     * 查询课程表总数
     */
    public int count() {
        String sql = "SELECT COUNT(*) FROM course_schedule";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    /**
     * 根据学期ID和班级名称查询周课表
     */
    public List<CourseSchedule> findWeekScheduleByClass(Integer semesterId, String className) {
        String sql = "SELECT * FROM course_schedule WHERE semester_id = ? AND class_name = ? ORDER BY week_day, class_section";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(CourseSchedule.class), semesterId, className);
    }
}