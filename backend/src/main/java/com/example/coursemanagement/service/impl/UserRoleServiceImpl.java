package com.example.coursemanagement.service.impl;

import com.example.coursemanagement.entity.UserRole;
import com.example.coursemanagement.repository.UserRoleRepository;
import com.example.coursemanagement.repository.RoleProgramRepository;
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
    
    @Autowired
    private RoleProgramRepository roleProgramRepository;

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
            // 检查该角色是否为教师角色且已绑定专业
            boolean isTeacherWithProgram = false;
            try {
                // 获取角色已绑定的专业数量
                isTeacherWithProgram = !roleProgramRepository.findByRoleId(roleId).isEmpty();
            } catch (Exception e) {
                // 忽略异常，继续执行
            }
            
            if (isTeacherWithProgram) {
                // 如果是有负责专业的教师角色，检查是否已有其他用户绑定
                List<UserRole> existingUsers = userRoleRepository.findByRoleId(roleId);
                if (!existingUsers.isEmpty()) {
                    // 检查是否是当前用户自己
                    boolean isCurrentUser = existingUsers.stream().anyMatch(ur -> ur.getUserId().equals(userId));
                    if (!isCurrentUser) {
                        throw new RuntimeException("该教师角色已绑定专业，只能分配给一个用户");
                    }
                }
            }
            
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