package com.example.coursemanagement.repository;

import com.example.coursemanagement.entity.RolePermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 角色权限关联数据访问层，使用Spring JDBC Template实现
 */
@Repository
public class RolePermissionRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 根据角色ID查询权限列表
     */
    public List<RolePermission> findByRoleId(Integer roleId) {
        String sql = "SELECT * FROM role_permission WHERE role_id = ?";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(RolePermission.class), roleId);
    }

    /**
     * 根据权限ID查询角色列表
     */
    public List<RolePermission> findByPermissionId(Integer permissionId) {
        String sql = "SELECT * FROM role_permission WHERE permission_id = ?";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(RolePermission.class), permissionId);
    }

    /**
     * 为角色添加权限
     */
    public int save(RolePermission rolePermission) {
        String sql = "INSERT INTO role_permission (role_id, permission_id) VALUES (?, ?)";
        return jdbcTemplate.update(sql, rolePermission.getRoleId(), rolePermission.getPermissionId());
    }

    /**
     * 删除角色的所有权限
     */
    public int deleteByRoleId(Integer roleId) {
        String sql = "DELETE FROM role_permission WHERE role_id = ?";
        return jdbcTemplate.update(sql, roleId);
    }

    /**
     * 删除权限的所有角色关联
     */
    public int deleteByPermissionId(Integer permissionId) {
        String sql = "DELETE FROM role_permission WHERE permission_id = ?";
        return jdbcTemplate.update(sql, permissionId);
    }
}
