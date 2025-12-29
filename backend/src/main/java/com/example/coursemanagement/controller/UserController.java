package com.example.coursemanagement.controller;

import com.example.coursemanagement.entity.User;
import com.example.coursemanagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户管理控制器
 */
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 查询所有用户
     */
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.findAll();
        return ResponseEntity.ok(users);
    }

    /**
     * 根据ID查询用户
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Integer id) {
        return userService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 新增用户
     */
    @PostMapping
    public ResponseEntity<Integer> createUser(@RequestBody User user) {
        int result = userService.save(user);
        return ResponseEntity.ok(result);
    }

    /**
     * 更新用户
     */
    @PutMapping("/{id}")
    public ResponseEntity<Integer> updateUser(@PathVariable Integer id, @RequestBody User user) {
        user.setUserId(id);
        int result = userService.update(user);
        return ResponseEntity.ok(result);
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Integer> deleteUser(@PathVariable Integer id) {
        int result = userService.deleteById(id);
        return ResponseEntity.ok(result);
    }
}
