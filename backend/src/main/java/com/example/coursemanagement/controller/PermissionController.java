package com.example.coursemanagement.controller;

import com.example.coursemanagement.entity.Permission;
import com.example.coursemanagement.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 权限管理控制器
 */
@RestController
@RequestMapping("/permissions")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    /**
     * 查询所有权限
     */
    @GetMapping
    @PreAuthorize("hasAuthority('permission:list')")
    public ResponseEntity<List<Permission>> getAllPermissions() {
        List<Permission> permissions = permissionService.findAll();
        return ResponseEntity.ok(permissions);
    }

    /**
     * 根据ID查询权限
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('permission:list')")
    public ResponseEntity<Permission> getPermissionById(@PathVariable Integer id) {
        return permissionService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 新增权限
     */
    @PostMapping
    @PreAuthorize("hasAuthority('permission:add')")
    public ResponseEntity<Integer> createPermission(@RequestBody Permission permission) {
        int result = permissionService.save(permission);
        return ResponseEntity.ok(result);
    }

    /**
     * 更新权限
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('permission:edit')")
    public ResponseEntity<Integer> updatePermission(@PathVariable Integer id, @RequestBody Permission permission) {
        permission.setPermissionId(id);
        int result = permissionService.update(permission);
        return ResponseEntity.ok(result);
    }

    /**
     * 删除权限
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('permission:delete')")
    public ResponseEntity<Integer> deletePermission(@PathVariable Integer id) {
        int result = permissionService.deleteById(id);
        return ResponseEntity.ok(result);
    }
}
