package com.example.coursemanagement.service;

import com.example.coursemanagement.entity.Role;
import java.util.List;
import java.util.Optional;

/**
 * 角色服务接口
 */
public interface RoleService {

    /**
     * 查询所有角色
     */
    List<Role> findAll();

    /**
     * 根据ID查询角色
     */
    Optional<Role> findById(Integer id);

    /**
     * 新增角色
     */
    int save(Role role);

    /**
     * 更新角色
     */
    int update(Role role);

    /**
     * 删除角色
     */
    int deleteById(Integer id);
}
