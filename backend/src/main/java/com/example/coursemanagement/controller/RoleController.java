package com.example.coursemanagement.controller;

import com.example.coursemanagement.entity.Role;
import com.example.coursemanagement.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理控制器
 */
@RestController
@RequestMapping("/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    /**
     * 查询所有角色
     */
    @GetMapping
    public ResponseEntity<List<Role>> getAllRoles() {
        List<Role> roles = roleService.findAll();
        return ResponseEntity.ok(roles);
    }

    /**
     * 根据ID查询角色
     */
    @GetMapping("/{id}")
    public ResponseEntity<Role> getRoleById(@PathVariable Integer id) {
        return roleService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 新增角色
     */
    @PostMapping
    public ResponseEntity<Integer> createRole(@RequestBody Role role) {
        int result = roleService.save(role);
        return ResponseEntity.ok(result);
    }

    /**
     * 更新角色
     */
    @PutMapping("/{id}")
    public ResponseEntity<Integer> updateRole(@PathVariable Integer id, @RequestBody Role role) {
        role.setRoleId(id);
        int result = roleService.update(role);
        return ResponseEntity.ok(result);
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Integer> deleteRole(@PathVariable Integer id) {
        int result = roleService.deleteById(id);
        return ResponseEntity.ok(result);
    }
}
