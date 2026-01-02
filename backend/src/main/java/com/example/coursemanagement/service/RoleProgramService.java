package com.example.coursemanagement.service;

import com.example.coursemanagement.entity.RoleProgram;

import java.util.List;

/**
 * 角色专业关联服务接口
 */
public interface RoleProgramService {

    /**
     * 根据角色ID查询关联的专业
     * @param roleId 角色ID
     * @return 角色专业关联列表
     */
    List<RoleProgram> findByRoleId(Integer roleId);

    /**
     * 为角色分配专业
     * @param roleId 角色ID
     * @param programIds 专业ID列表
     * @return 操作结果
     */
    int assignPrograms(Integer roleId, List<Integer> programIds);
}