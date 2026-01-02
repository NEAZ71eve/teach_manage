package com.example.coursemanagement.service.impl;

import com.example.coursemanagement.entity.User;
import com.example.coursemanagement.repository.UserRepository;
import com.example.coursemanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> findById(Integer id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public int save(User user) {
        return userRepository.save(user);
    }

    @Override
    public int update(User user) {
        return userRepository.update(user);
    }

    @Override
    public int deleteById(Integer id) {
        return userRepository.deleteById(id);
    }
}
