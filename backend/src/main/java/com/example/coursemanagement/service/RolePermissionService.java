package com.example.coursemanagement.service;

import com.example.coursemanagement.entity.RolePermission;

import java.util.List;

/**
 * 角色权限关联服务接口
 */
public interface RolePermissionService {

    /**
     * 根据角色ID查询权限列表
     * @param roleId 角色ID
     * @return 权限列表
     */
    List<RolePermission> findByRoleId(Integer roleId);

    /**
     * 为角色分配权限
     * @param roleId 角色ID
     * @param permissionIds 权限ID列表
     * @return 操作结果
     */
    int assignPermissions(Integer roleId, List<Integer> permissionIds);

    /**
     * 删除角色的所有权限
     * @param roleId 角色ID
     * @return 操作结果
     */
    int deleteByRoleId(Integer roleId);
}