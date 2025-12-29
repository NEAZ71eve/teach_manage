package com.example.coursemanagement.repository;

import com.example.coursemanagement.entity.ExamPaper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

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
    public Optional<ExamPaper> findById(String id) {
        String sql = "SELECT * FROM exam_paper WHERE paper_id = ?";
        List<ExamPaper> papers = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(ExamPaper.class), id);
        return papers.isEmpty() ? Optional.empty() : Optional.of(papers.get(0));
    }

    /**
     * 根据课程ID查询试卷
     */
    public List<ExamPaper> findByCourseId(String courseId) {
        String sql = "SELECT * FROM exam_paper WHERE program_id = ?";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(ExamPaper.class), courseId);
    }

    /**
     * 新增试卷
     */
    public int save(ExamPaper paper) {
        String sql = "INSERT INTO exam_paper (paper_id, paper_name, program_id, total_score, create_teacher, create_time) VALUES (?, ?, ?, ?, ?, NOW())";
        // 生成唯一的试卷ID
        String paperId = "PAPER_" + System.currentTimeMillis();
        paper.setPaperId(paperId);
        return jdbcTemplate.update(sql, 
                paperId,
                paper.getPaperName(), 
                paper.getProgramId(), 
                paper.getTotalScore(), 
                paper.getCreateTeacher());
    }

    /**
     * 更新试卷
     */
    public int update(ExamPaper paper) {
        String sql = "UPDATE exam_paper SET paper_name = ?, program_id = ?, total_score = ? WHERE paper_id = ?";
        return jdbcTemplate.update(sql, 
                paper.getPaperName(), 
                paper.getProgramId(), 
                paper.getTotalScore(), 
                paper.getPaperId());
    }

    /**
     * 删除试卷
     */
    public int deleteById(String id) {
        String sql = "DELETE FROM exam_paper WHERE paper_id = ?";
        return jdbcTemplate.update(sql, id);
    }

    /**
     * 批量删除试卷
     */
    public int deleteBatch(List<String> ids) {
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
