package com.example.coursemanagement.service.impl;

import com.example.coursemanagement.entity.RolePermission;
import com.example.coursemanagement.repository.RolePermissionRepository;
import com.example.coursemanagement.service.RolePermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 角色权限关联服务实现类
 */
@Service
public class RolePermissionServiceImpl implements RolePermissionService {

    @Autowired
    private RolePermissionRepository rolePermissionRepository;

    @Override
    public List<RolePermission> findByRoleId(Integer roleId) {
        return rolePermissionRepository.findByRoleId(roleId);
    }

    @Override
    public int assignPermissions(Integer roleId, List<Integer> permissionIds) {
        // 先删除角色已有的所有权限
        rolePermissionRepository.deleteByRoleId(roleId);
        
        // 为角色分配新权限
        int count = 0;
        for (Integer permissionId : permissionIds) {
            RolePermission rolePermission = new RolePermission();
            rolePermission.setRoleId(roleId);
            rolePermission.setPermissionId(permissionId);
            count += rolePermissionRepository.save(rolePermission);
        }
        
        return count;
    }

    @Override
    public int deleteByRoleId(Integer roleId) {
        return rolePermissionRepository.deleteByRoleId(roleId);
    }
}