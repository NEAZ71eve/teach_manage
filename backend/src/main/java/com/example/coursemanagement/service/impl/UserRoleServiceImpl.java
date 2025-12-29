package com.example.coursemanagement.service.impl;

import com.example.coursemanagement.entity.UserRole;
import com.example.coursemanagement.repository.UserRoleRepository;
import com.example.coursemanagement.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户角色关联服务实现类
 */
@Service
public class UserRoleServiceImpl implements UserRoleService {

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Override
    public List<UserRole> findByUserId(Integer userId) {
        return userRoleRepository.findByUserId(userId);
    }

    @Override
    public int assignRoles(Integer userId, List<Integer> roleIds) {
        // 先删除用户已有的所有角色
        userRoleRepository.deleteByUserId(userId);
        
        // 为用户分配新角色
        int count = 0;
        for (Integer roleId : roleIds) {
            UserRole userRole = new UserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            count += userRoleRepository.save(userRole);
        }
        
        return count;
    }

    @Override
    public int deleteByUserId(Integer userId) {
        return userRoleRepository.deleteByUserId(userId);
    }
}