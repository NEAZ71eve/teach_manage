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
        // 使用正确的列名，基于实际数据库模式
        String sql = "INSERT INTO course (course_name, course_code, major_id, credit, total_hours, theory_hours, experiment_hours, design_hours, course_type_id, course_nature, course_category, exam_mark, program_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        System.out.println("执行SQL: " + sql);
        
        // 计算理论学时、实验学时和设计学时的默认值
        int theoryHours = course.getTheoreticalHours() != null ? course.getTheoreticalHours() : 0;
        int experimentHours = course.getPracticalHours() != null ? course.getPracticalHours() : 0;
        int designHours = 0; // 默认设计学时为0
        
        // 获取courseType的值，优先使用courseTypeId，如果没有则使用courseType转换
        Integer courseTypeId = course.getCourseTypeId();
        if (courseTypeId == null && course.getCourseType() != null) {
            try {
                courseTypeId = Integer.parseInt(course.getCourseType());
            } catch (NumberFormatException e) {
                courseTypeId = 1; // 默认值
            }
        } else if (courseTypeId == null) {
            courseTypeId = 1; // 默认值
        }
        
        return jdbcTemplate.update(sql, 
                course.getCourseName(), 
                course.getCourseCode(), 
                course.getMajorId(), 
                course.getCredit(), 
                course.getTotalHours(), 
                theoryHours, 
                experimentHours, 
                designHours, 
                courseTypeId, 
                course.getCourseNature(),
                course.getCourseCategory(),
                course.getExamMark(),
                course.getProgramId());
    }

    /**
     * 更新课程
     */
    public int update(Course course) {
        // 使用正确的列名，基于实际数据库模式
        String sql = "UPDATE course SET course_name = ?, course_code = ?, major_id = ?, credit = ?, total_hours = ?, theory_hours = ?, experiment_hours = ?, design_hours = ?, course_type_id = ?, course_nature = ?, course_category = ?, exam_mark = ?, program_id = ? WHERE course_id = ?";
        
        System.out.println("执行SQL: " + sql);
        
        // 计算理论学时、实验学时和设计学时
        int theoryHours = course.getTheoreticalHours() != null ? course.getTheoreticalHours() : 0;
        int experimentHours = course.getPracticalHours() != null ? course.getPracticalHours() : 0;
        int designHours = 0; // 默认设计学时为0
        
        // 获取courseType的值，优先使用courseTypeId，如果没有则使用courseType转换
        Integer courseTypeId = course.getCourseTypeId();
        if (courseTypeId == null && course.getCourseType() != null) {
            try {
                courseTypeId = Integer.parseInt(course.getCourseType());
            } catch (NumberFormatException e) {
                courseTypeId = 1; // 默认值
            }
        } else if (courseTypeId == null) {
            courseTypeId = 1; // 默认值
        }
        
        return jdbcTemplate.update(sql, 
                course.getCourseName(), 
                course.getCourseCode(), 
                course.getMajorId(), 
                course.getCredit(), 
                course.getTotalHours(), 
                theoryHours, 
                experimentHours, 
                designHours, 
                courseTypeId, 
                course.getCourseNature(),
                course.getCourseCategory(),
                course.getExamMark(),
                course.getProgramId(),
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
    
    /**
     * 根据培养方案ID查询课程列表
     */
    public List<Course> findByProgramId(Integer programId) {
        String sql = "SELECT * FROM course WHERE program_id = ?";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Course.class), programId);
    }
    
    /**
     * 根据专业ID查询课程列表
     */
    public List<Course> findByMajorId(Integer majorId) {
        String sql = "SELECT * FROM course WHERE major_id = ?";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Course.class), majorId);
    }
    
    /**
     * 根据培养方案ID和学期ID查询课程列表
     */
    public List<Course> findByProgramIdAndSemesterId(Integer programId, Integer semesterId) {
        String sql = "SELECT c.* FROM course c JOIN course_semester cs ON c.course_id = cs.course_id WHERE c.program_id = ? AND cs.semester_id = ?";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Course.class), programId, semesterId);
    }
    
    /**
     * 根据专业ID和学期ID查询课程列表
     */
    public List<Course> findByMajorIdAndSemesterId(Integer majorId, Integer semesterId) {
        String sql = "SELECT c.* FROM course c JOIN course_semester cs ON c.course_id = cs.course_id WHERE c.major_id = ? AND cs.semester_id = ?";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Course.class), majorId, semesterId);
    }
}
