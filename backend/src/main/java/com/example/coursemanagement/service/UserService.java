package com.example.coursemanagement.service;

import com.example.coursemanagement.entity.User;
import java.util.List;
import java.util.Optional;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 查询所有用户
     */
    List<User> findAll();

    /**
     * 根据ID查询用户
     */
    Optional<User> findById(Integer id);

    /**
     * 根据用户名查询用户
     */
    Optional<User> findByUsername(String username);

    /**
     * 新增用户
     */
    int save(User user);

    /**
     * 更新用户
     */
    int update(User user);

    /**
     * 删除用户
     */
    int deleteById(Integer id);
}
