package com.example.coursemanagement.controller;

import com.example.coursemanagement.entity.User;
import com.example.coursemanagement.entity.UserRole;
import com.example.coursemanagement.service.RoleService;
import com.example.coursemanagement.service.UserRoleService;
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
    
    @Autowired
    private RoleService roleService;
    
    @Autowired
    private UserRoleService userRoleService;

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
        try {
            int result = userService.save(user);
            return ResponseEntity.ok(result);
        } catch (org.springframework.dao.DuplicateKeyException e) {
            // 捕获用户名重复异常
            throw new RuntimeException("用户名已存在，请使用其他用户名");
        }
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
    
    /**
     * 根据用户ID查询角色列表
     */
    @GetMapping("/{id}/roles")
    public ResponseEntity<List<UserRole>> getUserRoles(@PathVariable Integer id) {
        List<UserRole> userRoles = userRoleService.findByUserId(id);
        return ResponseEntity.ok(userRoles);
    }
    
    /**
     * 为用户分配角色
     */
    @PostMapping("/{id}/roles")
    public ResponseEntity<Integer> assignRoles(@PathVariable Integer id, @RequestBody List<Integer> roleIds) {
        int result = userRoleService.assignRoles(id, roleIds);
        return ResponseEntity.ok(result);
    }
}
