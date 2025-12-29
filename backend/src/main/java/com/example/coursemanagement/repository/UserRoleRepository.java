package com.example.coursemanagement.repository;

import com.example.coursemanagement.entity.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用户角色关联数据访问层，使用Spring JDBC Template实现
 */
@Repository
public class UserRoleRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 根据用户ID查询角色列表
     */
    public List<UserRole> findByUserId(Integer userId) {
        String sql = "SELECT * FROM user_role WHERE user_id = ?";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(UserRole.class), userId);
    }

    /**
     * 根据角色ID查询用户列表
     */
    public List<UserRole> findByRoleId(Integer roleId) {
        String sql = "SELECT * FROM user_role WHERE role_id = ?";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(UserRole.class), roleId);
    }

    /**
     * 为用户添加角色
     */
    public int save(UserRole userRole) {
        String sql = "INSERT INTO user_role (user_id, role_id) VALUES (?, ?)";
        return jdbcTemplate.update(sql, userRole.getUserId(), userRole.getRoleId());
    }

    /**
     * 删除用户的所有角色
     */
    public int deleteByUserId(Integer userId) {
        String sql = "DELETE FROM user_role WHERE user_id = ?";
        return jdbcTemplate.update(sql, userId);
    }

    /**
     * 删除角色的所有用户关联
     */
    public int deleteByRoleId(Integer roleId) {
        String sql = "DELETE FROM user_role WHERE role_id = ?";
        return jdbcTemplate.update(sql, roleId);
    }
}
