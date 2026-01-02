package com.example.coursemanagement.repository;

import com.example.coursemanagement.entity.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 权限数据访问层，使用Spring JDBC Template实现
 */
@Repository
public class PermissionRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 查询所有权限
     */
    public List<Permission> findAll() {
        String sql = "SELECT * FROM permission";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Permission.class));
    }

    /**
     * 根据ID查询权限
     */
    public Optional<Permission> findById(Integer id) {
        String sql = "SELECT * FROM permission WHERE permission_id = ?";
        List<Permission> permissions = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Permission.class), id);
        return permissions.isEmpty() ? Optional.empty() : Optional.of(permissions.get(0));
    }

    /**
     * 新增权限
     */
    public int save(Permission permission) {
        String sql = "INSERT INTO permission (permission_name, permission_code, description, url, method) VALUES (?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql, 
                permission.getPermissionName(), 
                permission.getPermissionCode(), 
                permission.getDescription(), 
                permission.getUrl(), 
                permission.getMethod());
    }

    /**
     * 更新权限
     */
    public int update(Permission permission) {
        String sql = "UPDATE permission SET permission_name = ?, permission_code = ?, description = ?, url = ?, method = ? WHERE permission_id = ?";
        return jdbcTemplate.update(sql, 
                permission.getPermissionName(), 
                permission.getPermissionCode(), 
                permission.getDescription(), 
                permission.getUrl(), 
                permission.getMethod(),
                permission.getPermissionId());
    }

    /**
     * 删除权限
     */
    public int deleteById(Integer id) {
        String sql = "DELETE FROM permission WHERE permission_id = ?";
        return jdbcTemplate.update(sql, id);
    }
}
