package com.example.coursemanagement.service;

import com.example.coursemanagement.entity.UserRole;

import java.util.List;

/**
 * 用户角色关联服务接口
 */
public interface UserRoleService {

    /**
     * 根据用户ID查询角色列表
     * @param userId 用户ID
     * @return 角色列表
     */
    List<UserRole> findByUserId(Integer userId);

    /**
     * 为用户分配角色
     * @param userId 用户ID
     * @param roleIds 角色ID列表
     * @return 操作结果
     */
    int assignRoles(Integer userId, List<Integer> roleIds);

    /**
     * 删除用户的所有角色
     * @param userId 用户ID
     * @return 操作结果
     */
    int deleteByUserId(Integer userId);
}