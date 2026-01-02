package com.example.coursemanagement.repository;

import com.example.coursemanagement.entity.TrainingProgram;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 培养方案数据访问层，使用Spring JDBC Template实现
 */
@Repository
public class TrainingProgramRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private volatile Boolean descriptionColumnAvailable;

    /**
     * 查询所有培养方案
     */
    public List<TrainingProgram> findAll() {
        String sql = "SELECT * FROM training_program";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(TrainingProgram.class));
    }

    /**
     * 根据ID查询培养方案
     */
    public Optional<TrainingProgram> findById(Integer id) {
        String sql = "SELECT * FROM training_program WHERE program_id = ?";
        List<TrainingProgram> programs = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(TrainingProgram.class), id);
        return programs.isEmpty() ? Optional.empty() : Optional.of(programs.get(0));
    }

    /**
     * 新增培养方案
     */
    public int save(TrainingProgram program) {
        if (isDescriptionColumnAvailable()) {
            String sql = "INSERT INTO training_program (major_name, duration, total_credit, effective_year, description, create_time, update_time) VALUES (?, ?, ?, ?, ?, NOW(), NOW())";
            return jdbcTemplate.update(sql,
                    program.getMajorName(),
                    program.getDuration(),
                    program.getTotalCredit(),
                    program.getEffectiveYear(),
                    program.getDescription());
        }

        String fallbackSql = "INSERT INTO training_program (major_name, duration, total_credit, effective_year, create_time, update_time) VALUES (?, ?, ?, ?, NOW(), NOW())";
        return jdbcTemplate.update(fallbackSql,
                program.getMajorName(),
                program.getDuration(),
                program.getTotalCredit(),
                program.getEffectiveYear());
    }

    /**
     * 更新培养方案
     */
    public int update(TrainingProgram program) {
        if (isDescriptionColumnAvailable()) {
            String sql = "UPDATE training_program SET major_name = ?, duration = ?, total_credit = ?, effective_year = ?, description = ?, update_time = NOW() WHERE program_id = ?";
            return jdbcTemplate.update(sql,
                    program.getMajorName(),
                    program.getDuration(),
                    program.getTotalCredit(),
                    program.getEffectiveYear(),
                    program.getDescription(),
                    program.getProgramId());
        }

        String fallbackSql = "UPDATE training_program SET major_name = ?, duration = ?, total_credit = ?, effective_year = ?, update_time = NOW() WHERE program_id = ?";
        return jdbcTemplate.update(fallbackSql,
                program.getMajorName(),
                program.getDuration(),
                program.getTotalCredit(),
                program.getEffectiveYear(),
                program.getProgramId());
    }

    private boolean isDescriptionColumnAvailable() {
        if (descriptionColumnAvailable != null) {
            return descriptionColumnAvailable;
        }
        synchronized (this) {
            if (descriptionColumnAvailable != null) {
                return descriptionColumnAvailable;
            }
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 'training_program' AND column_name = 'description'",
                    Integer.class
            );
            descriptionColumnAvailable = count != null && count > 0;
            return descriptionColumnAvailable;
        }
    }

    /**
     * 删除培养方案
     */
    public int deleteById(Integer id) {
        String sql = "DELETE FROM training_program WHERE program_id = ?";
        return jdbcTemplate.update(sql, id);
    }

    /**
     * 批量删除培养方案
     */
    public int deleteBatch(List<Integer> ids) {
        String sql = "DELETE FROM training_program WHERE program_id IN (" + ids.stream().map(id -> "?").reduce((a, b) -> a + "," + b).orElse("") + ")";
        return jdbcTemplate.update(sql, ids.toArray());
    }

    /**
     * 分页查询培养方案
     */
    public List<TrainingProgram> findByPage(int page, int limit) {
        int offset = (page - 1) * limit;
        String sql = "SELECT * FROM training_program LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(TrainingProgram.class), limit, offset);
    }

    /**
     * 查询培养方案总数
     */
    public int count() {
        String sql = "SELECT COUNT(*) FROM training_program";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }
    
    /**
     * 根据老师ID查询培养方案列表
     */
    public List<TrainingProgram> findByTeacherId(Integer teacherId) {
        // 由于表中没有teacher_id字段，返回空列表或所有记录
        // 这里返回所有记录作为临时解决方案
        return findAll();
    }
}
