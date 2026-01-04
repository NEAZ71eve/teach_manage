package com.example.coursemanagement.repository;

import com.example.coursemanagement.entity.User;
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
        String sql = "SELECT u.*, r.role_id AS role_id, r.role_name AS role_name " +
                "FROM user u " +
                "LEFT JOIN user_role ur ON u.user_id = ur.user_id " +
                "LEFT JOIN role r ON ur.role_id = r.role_id";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(User.class));
    }

    /**
     * 根据ID查询用户
     */
    public Optional<User> findById(Integer id) {
        String sql = "SELECT u.*, r.role_id AS role_id, r.role_name AS role_name " +
                "FROM user u " +
                "LEFT JOIN user_role ur ON u.user_id = ur.user_id " +
                "LEFT JOIN role r ON ur.role_id = r.role_id " +
                "WHERE u.user_id = ?";
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
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getRealName());
            statement.setString(4, user.getEmail());
            statement.setString(5, user.getPhone());
            statement.setInt(6, user.getStatus());
            if (user.getProgramId() == null) {
                statement.setNull(7, java.sql.Types.INTEGER);
            } else {
                statement.setInt(7, user.getProgramId());
            }
            return statement;
        }, keyHolder);
        Number key = keyHolder.getKey();
        return key == null ? 0 : key.intValue();
    }

    /**
     * 更新用户
     */
    public int update(User user) {
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            String sql = "UPDATE user SET username = ?, password = ?, real_name = ?, email = ?, phone = ?, status = ?, program_id = ?, update_time = NOW() WHERE user_id = ?";
            return jdbcTemplate.update(sql, 
                    user.getUsername(),
                    user.getPassword(),
                    user.getRealName(),
                    user.getEmail(),
                    user.getPhone(),
                    user.getStatus(),
                    user.getProgramId(),
                    user.getUserId());
        } else {
            String sql = "UPDATE user SET username = ?, real_name = ?, email = ?, phone = ?, status = ?, program_id = ?, update_time = NOW() WHERE user_id = ?";
            return jdbcTemplate.update(sql, 
                    user.getUsername(),
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

    public int updateProgramId(Integer userId, Integer programId) {
        String sql = "UPDATE user SET program_id = ?, update_time = NOW() WHERE user_id = ?";
        return jdbcTemplate.update(sql, programId, userId);
    }
}
