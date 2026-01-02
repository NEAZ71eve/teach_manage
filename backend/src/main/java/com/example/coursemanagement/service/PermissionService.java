package com.example.coursemanagement.service;

import com.example.coursemanagement.entity.Permission;
import java.util.List;
import java.util.Optional;

/**
 * 权限服务接口
 */
public interface PermissionService {

    /**
     * 查询所有权限
     */
    List<Permission> findAll();

    /**
     * 根据ID查询权限
     */
    Optional<Permission> findById(Integer id);

    /**
     * 新增权限
     */
    int save(Permission permission);

    /**
     * 更新权限
     */
    int update(Permission permission);

    /**
     * 删除权限
     */
    int deleteById(Integer id);
}
