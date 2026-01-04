package com.example.coursemanagement.service.impl;

import com.example.coursemanagement.entity.UserRole;
import com.example.coursemanagement.repository.UserRoleRepository;
import com.example.coursemanagement.repository.RoleRepository;
import com.example.coursemanagement.repository.UserRepository;
import com.example.coursemanagement.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 用户角色关联服务实现类
 */
@Service
public class UserRoleServiceImpl implements UserRoleService {

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<UserRole> findByUserId(Integer userId) {
        return userRoleRepository.findByUserId(userId);
    }

    @Override
    public int assignRoles(Integer userId, List<Integer> roleIds) {
        if (roleIds == null || roleIds.size() != 1) {
            throw new RuntimeException("用户必须且只能选择一个角色");
        }

        Integer roleId = roleIds.get(0);
        String roleName = roleRepository.findById(roleId)
                .map(role -> role.getRoleName())
                .orElseThrow(() -> new RuntimeException("角色不存在"));

        if ("学院管理员".equals(roleName)) {
            Optional<UserRole> existingProgramTeacher = userRoleRepository.findByRoleId(roleId).stream()
                    .filter(userRole -> !userRole.getUserId().equals(userId))
                    .findFirst();
            if (existingProgramTeacher.isPresent()) {
                throw new RuntimeException("专业负责教师只能绑定一个用户");
            }
            Optional<com.example.coursemanagement.entity.User> userOptional = userRepository.findById(userId);
            if (userOptional.isPresent() && userOptional.get().getProgramId() == null) {
                throw new RuntimeException("专业负责教师必须绑定负责专业");
            }
        } else {
            userRepository.updateProgramId(userId, null);
        }

        // 先删除用户已有的所有角色
        userRoleRepository.deleteByUserId(userId);
        
        // 为用户分配新角色
        UserRole userRole = new UserRole();
        userRole.setUserId(userId);
        userRole.setRoleId(roleId);
        return userRoleRepository.save(userRole);
    }

    @Override
    public int deleteByUserId(Integer userId) {
        return userRoleRepository.deleteByUserId(userId);
    }
}
