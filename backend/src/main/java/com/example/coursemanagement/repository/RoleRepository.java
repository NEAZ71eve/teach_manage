package com.example.coursemanagement.repository;

import com.example.coursemanagement.entity.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 角色数据访问层，使用Spring JDBC Template实现
 */
@Repository
public class RoleRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 查询所有角色
     */
    public List<Role> findAll() {
        String sql = "SELECT * FROM role";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Role.class));
    }

    /**
     * 根据ID查询角色
     */
    public Optional<Role> findById(Integer id) {
        String sql = "SELECT * FROM role WHERE role_id = ?";
        List<Role> roles = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Role.class), id);
        return roles.isEmpty() ? Optional.empty() : Optional.of(roles.get(0));
    }

    /**
     * 新增角色
     */
    public int save(Role role) {
        String sql = "INSERT INTO role (role_name, role_desc) VALUES (?, ?)";
        return jdbcTemplate.update(sql, role.getRoleName(), role.getRoleDesc());
    }

    /**
     * 更新角色
     */
    public int update(Role role) {
        String sql = "UPDATE role SET role_name = ?, role_desc = ? WHERE role_id = ?";
        return jdbcTemplate.update(sql, role.getRoleName(), role.getRoleDesc(), role.getRoleId());
    }

    /**
     * 删除角色
     */
    public int deleteById(Integer id) {
        String sql = "DELETE FROM role WHERE role_id = ?";
        return jdbcTemplate.update(sql, id);
    }
}
