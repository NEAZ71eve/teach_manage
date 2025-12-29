package com.example.coursemanagement.controller;

import com.example.coursemanagement.entity.Role;
import com.example.coursemanagement.entity.RolePermission;
import com.example.coursemanagement.entity.RoleProgram;
import com.example.coursemanagement.service.PermissionService;
import com.example.coursemanagement.service.RolePermissionService;
import com.example.coursemanagement.service.RoleProgramService;
import com.example.coursemanagement.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 角色管理控制器
 */
@RestController
@RequestMapping("/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;
    
    @Autowired
    private PermissionService permissionService;
    
    @Autowired
    private RolePermissionService rolePermissionService;
    
    @Autowired
    private RoleProgramService roleProgramService;

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
    
    /**
     * 根据角色ID查询权限列表
     */
    @GetMapping("/{id}/permissions")
    public ResponseEntity<List<RolePermission>> getRolePermissions(@PathVariable Integer id) {
        List<RolePermission> rolePermissions = rolePermissionService.findByRoleId(id);
        return ResponseEntity.ok(rolePermissions);
    }
    
    /**
     * 为角色分配权限
     */
    @PostMapping("/{id}/permissions")
    public ResponseEntity<Integer> assignPermissions(@PathVariable Integer id, @RequestBody List<Integer> permissionIds) {
        int result = rolePermissionService.assignPermissions(id, permissionIds);
        return ResponseEntity.ok(result);
    }
    
    /**
     * 根据角色ID查询关联的专业
     */
    @GetMapping("/{id}/programs")
    public ResponseEntity<List<RoleProgram>> getRolePrograms(@PathVariable Integer id) {
        List<RoleProgram> rolePrograms = roleProgramService.findByRoleId(id);
        return ResponseEntity.ok(rolePrograms);
    }
    
    /**
     * 为角色分配专业
     */
    @PostMapping("/{id}/programs")
    public ResponseEntity<Integer> assignPrograms(@PathVariable Integer id, @RequestBody List<Integer> programIds) {
        int result = roleProgramService.assignPrograms(id, programIds);
        return ResponseEntity.ok(result);
    }
    
    /**
     * 获取所有角色-专业关联
     */
    @GetMapping("/programs")
    public ResponseEntity<List<RoleProgram>> getAllRolePrograms() {
        List<RoleProgram> allRolePrograms = new ArrayList<>();
        List<Role> roles = roleService.findAll();
        for (Role role : roles) {
            List<RoleProgram> rolePrograms = roleProgramService.findByRoleId(role.getRoleId());
            allRolePrograms.addAll(rolePrograms);
        }
        return ResponseEntity.ok(allRolePrograms);
    }
}
