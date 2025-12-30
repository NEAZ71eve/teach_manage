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
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasAuthority('role:list')")
    public ResponseEntity<List<Role>> getAllRoles() {
        List<Role> roles = roleService.findAll();
        return ResponseEntity.ok(roles);
    }

    /**
     * 根据ID查询角色
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('role:list')")
    public ResponseEntity<Role> getRoleById(@PathVariable Integer id) {
        return roleService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 更新角色 - 只能更新教师角色的描述和专业绑定
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('role:edit')")
    public ResponseEntity<Integer> updateRole(@PathVariable Integer id, @RequestBody Role role) {
        // 只能更新现有角色，不能创建新角色
        role.setRoleId(id);
        int result = roleService.update(role);
        return ResponseEntity.ok(result);
    }
    
    /**
     * 根据角色ID查询权限列表
     */
    @GetMapping("/{id}/permissions")
    @PreAuthorize("hasAuthority('role:list')")
    public ResponseEntity<List<RolePermission>> getRolePermissions(@PathVariable Integer id) {
        List<RolePermission> rolePermissions = rolePermissionService.findByRoleId(id);
        return ResponseEntity.ok(rolePermissions);
    }
    
    /**
     * 为角色分配权限
     */
    @PostMapping("/{id}/permissions")
    @PreAuthorize("hasAuthority('role:edit')")
    public ResponseEntity<Integer> assignPermissions(@PathVariable Integer id, @RequestBody List<Integer> permissionIds) {
        int result = rolePermissionService.assignPermissions(id, permissionIds);
        return ResponseEntity.ok(result);
    }
    
    /**
     * 根据角色ID查询关联的专业
     */
    @GetMapping("/{id}/programs")
    @PreAuthorize("hasAuthority('role:list')")
    public ResponseEntity<List<RoleProgram>> getRolePrograms(@PathVariable Integer id) {
        List<RoleProgram> rolePrograms = roleProgramService.findByRoleId(id);
        return ResponseEntity.ok(rolePrograms);
    }
    
    /**
     * 为角色分配专业
     */
    @PostMapping("/{id}/programs")
    @PreAuthorize("hasAuthority('role:edit')")
    public ResponseEntity<Integer> assignPrograms(@PathVariable Integer id, @RequestBody List<Integer> programIds) {
        int result = roleProgramService.assignPrograms(id, programIds);
        return ResponseEntity.ok(result);
    }
    
    /**
     * 获取所有角色-专业关联
     */
    @GetMapping("/programs")
    @PreAuthorize("hasAuthority('role:list')")
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
