package com.example.coursemanagement.repository;

import com.example.coursemanagement.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 用户数据访问层，使用Spring JDBC Template实现
 */
@Repository
public class UserRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 查询所有用户
     */
    public List<User> findAll() {
        String sql = "SELECT * FROM user";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(User.class));
    }

    /**
     * 根据ID查询用户
     */
    public Optional<User> findById(Integer id) {
        String sql = "SELECT * FROM user WHERE user_id = ?";
        List<User> users = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(User.class), id);
        return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
    }

    /**
     * 根据用户名查询用户
     */
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM user WHERE username = ?";
        List<User> users = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(User.class), username);
        return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
    }

    /**
     * 新增用户
     */
    public int save(User user) {
        String sql = "INSERT INTO user (username, password, real_name, email, phone, status, program_id, create_time, update_time) VALUES (?, ?, ?, ?, ?, ?, ?, NOW(), NOW())";
        return jdbcTemplate.update(sql, 
                user.getUsername(), 
                user.getPassword(), 
                user.getRealName(), 
                user.getEmail(), 
                user.getPhone(), 
                user.getStatus(),
                user.getProgramId());
    }

    /**
     * 更新用户
     */
    public int update(User user) {
        String sql;
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            sql = "UPDATE user SET real_name = ?, email = ?, phone = ?, status = ?, program_id = ?, update_time = NOW() WHERE user_id = ?";
            return jdbcTemplate.update(sql, 
                    user.getRealName(), 
                    user.getEmail(), 
                    user.getPhone(), 
                    user.getStatus(),
                    user.getProgramId(),
                    user.getUserId());
        } else {
            sql = "UPDATE user SET real_name = ?, email = ?, phone = ?, status = ?, program_id = ?, update_time = NOW() WHERE user_id = ?";
            return jdbcTemplate.update(sql, 
                    user.getRealName(), 
                    user.getEmail(), 
                    user.getPhone(), 
                    user.getStatus(),
                    user.getProgramId(),
                    user.getUserId());
        }
    }

    /**
     * 删除用户
     */
    public int deleteById(Integer id) {
        String sql = "DELETE FROM user WHERE user_id = ?";
        return jdbcTemplate.update(sql, id);
    }
}
