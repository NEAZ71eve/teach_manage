package com.example.coursemanagement.repository;

import com.example.coursemanagement.entity.ExamPaper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;

import java.util.List;
import java.util.Optional;

/**
 * 试卷数据访问层，使用Spring JDBC Template实现
 */
@Repository
public class ExamPaperRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 查询所有试卷
     */
    public List<ExamPaper> findAll() {
        String sql = "SELECT * FROM exam_paper";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(ExamPaper.class));
    }

    /**
     * 根据ID查询试卷
     */
    public Optional<ExamPaper> findById(Integer id) {
        String sql = "SELECT * FROM exam_paper WHERE paper_id = ?";
        List<ExamPaper> papers = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(ExamPaper.class), id);
        return papers.isEmpty() ? Optional.empty() : Optional.of(papers.get(0));
    }

    /**
     * 根据课程ID查询试卷
     */
    public List<ExamPaper> findByCourseId(Integer courseId) {
        String sql = "SELECT * FROM exam_paper WHERE course_id = ?";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(ExamPaper.class), courseId);
    }

    /**
     * 新增试卷
     */
    public int save(ExamPaper paper) {
        String sql = "INSERT INTO exam_paper (paper_name, course_id, total_score, paper_type, create_teacher, create_time, update_time) VALUES (?, ?, ?, ?, ?, NOW(), NOW())";
        
        // 使用GeneratedKeyHolder获取自动生成的主键
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int result = jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                    ps.setString(1, paper.getPaperName());
                    ps.setInt(2, paper.getCourseId());
                    ps.setDouble(3, paper.getTotalScore());
                    ps.setString(4, paper.getPaperType());
                    ps.setString(5, paper.getCreateTeacher() != null ? paper.getCreateTeacher() : "admin");
                    return ps;
                },
                keyHolder
        );
        
        // 设置生成的主键到实体对象
        paper.setPaperId(keyHolder.getKey().intValue());
        return result; // 返回实际影响的行数
    }

    /**
     * 更新试卷
     */
    public int update(ExamPaper paper) {
        String sql = "UPDATE exam_paper SET paper_name = ?, course_id = ?, total_score = ?, paper_type = ? WHERE paper_id = ?";
        return jdbcTemplate.update(sql, 
                paper.getPaperName(), 
                paper.getCourseId(), 
                paper.getTotalScore(), 
                paper.getPaperType(),
                paper.getPaperId());
    }

    /**
     * 删除试卷
     */
    public int deleteById(Integer id) {
        String sql = "DELETE FROM exam_paper WHERE paper_id = ?";
        return jdbcTemplate.update(sql, id);
    }

    /**
     * 批量删除试卷
     */
    public int deleteBatch(List<Integer> ids) {
        String sql = "DELETE FROM exam_paper WHERE paper_id IN (" + ids.stream().map(id -> "?").reduce((a, b) -> a + "," + b).orElse("") + ")";
        return jdbcTemplate.update(sql, ids.toArray());
    }

    /**
     * 分页查询试卷
     */
    public List<ExamPaper> findByPage(int page, int limit) {
        int offset = (page - 1) * limit;
        String sql = "SELECT * FROM exam_paper LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(ExamPaper.class), limit, offset);
    }

    /**
     * 查询试卷总数
     */
    public int count() {
        String sql = "SELECT COUNT(*) FROM exam_paper";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }
}
