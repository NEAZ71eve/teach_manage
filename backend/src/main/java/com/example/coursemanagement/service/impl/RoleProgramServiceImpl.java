package com.example.coursemanagement.service.impl;

import com.example.coursemanagement.entity.RoleProgram;
import com.example.coursemanagement.repository.RoleProgramRepository;
import com.example.coursemanagement.service.RoleProgramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 角色专业关联服务实现类
 */
@Service
public class RoleProgramServiceImpl implements RoleProgramService {

    @Autowired
    private RoleProgramRepository roleProgramRepository;

    @Override
    public List<RoleProgram> findByRoleId(Integer roleId) {
        return roleProgramRepository.findByRoleId(roleId);
    }

    @Override
    public int assignPrograms(Integer roleId, List<Integer> programIds) {
        // 先删除该角色所有现有关联
        roleProgramRepository.deleteByRoleId(roleId);
        
        // 如果没有专业ID，直接返回成功
        if (programIds == null || programIds.isEmpty()) {
            return 1;
        }
        
        // 检查每个专业是否已被绑定到其他角色
        for (Integer programId : programIds) {
            if (roleProgramRepository.isProgramBoundToOtherRole(programId, roleId)) {
                throw new RuntimeException("专业ID " + programId + " 已被其他角色绑定");
            }
        }
        
        // 批量添加新的关联
        List<RoleProgram> rolePrograms = new ArrayList<>();
        for (Integer programId : programIds) {
            RoleProgram rp = new RoleProgram();
            rp.setRoleId(roleId);
            rp.setProgramId(programId);
            rolePrograms.add(rp);
        }
        
        int result = roleProgramRepository.batchSave(rolePrograms);
        return result;
    }
}