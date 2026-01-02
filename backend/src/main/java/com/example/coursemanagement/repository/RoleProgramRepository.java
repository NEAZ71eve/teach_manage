package com.example.coursemanagement.repository;

import com.example.coursemanagement.entity.RoleProgram;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 角色专业关联数据访问层，使用Spring JDBC Template实现
 */
@Repository
public class RoleProgramRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 根据角色ID查询关联的专业
     */
    public List<RoleProgram> findByRoleId(Integer roleId) {
        String sql = "SELECT * FROM role_program WHERE role_id = ?";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(RoleProgram.class), roleId);
    }

    /**
     * 根据专业ID查询关联的角色
     */
    public List<RoleProgram> findByProgramId(Integer programId) {
        String sql = "SELECT * FROM role_program WHERE program_id = ?";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(RoleProgram.class), programId);
    }

    /**
     * 检查专业是否已被绑定到任何角色
     */
    public boolean isProgramBound(Integer programId) {
        String sql = "SELECT COUNT(*) FROM role_program WHERE program_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, programId);
        return count != null && count > 0;
    }

    /**
     * 检查专业是否已被绑定到除指定角色外的其他角色
     */
    public boolean isProgramBoundToOtherRole(Integer programId, Integer excludeRoleId) {
        String sql = "SELECT COUNT(*) FROM role_program WHERE program_id = ? AND role_id != ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, programId, excludeRoleId);
        return count != null && count > 0;
    }

    /**
     * 根据角色ID删除所有关联的专业
     */
    public int deleteByRoleId(Integer roleId) {
        String sql = "DELETE FROM role_program WHERE role_id = ?";
        return jdbcTemplate.update(sql, roleId);
    }

    /**
     * 批量添加角色专业关联
     */
    public int batchSave(List<RoleProgram> rolePrograms) {
        String sql = "INSERT INTO role_program (role_id, program_id) VALUES (?, ?)";
        int[][] results = jdbcTemplate.batchUpdate(sql, rolePrograms, rolePrograms.size(), (ps, rp) -> {
            ps.setInt(1, rp.getRoleId());
            ps.setInt(2, rp.getProgramId());
        });
        // 计算成功插入的总数
        int total = 0;
        for (int[] result : results) {
            for (int count : result) {
                total += count;
            }
        }
        return total;
    }
}