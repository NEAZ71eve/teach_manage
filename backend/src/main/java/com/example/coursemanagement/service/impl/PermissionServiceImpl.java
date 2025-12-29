package com.example.coursemanagement.service.impl;

import com.example.coursemanagement.entity.Permission;
import com.example.coursemanagement.repository.PermissionRepository;
import com.example.coursemanagement.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 权限服务实现类
 */
@Service
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private PermissionRepository permissionRepository;

    @Override
    public List<Permission> findAll() {
        return permissionRepository.findAll();
    }

    @Override
    public Optional<Permission> findById(Integer id) {
        return permissionRepository.findById(id);
    }

    @Override
    public int save(Permission permission) {
        return permissionRepository.save(permission);
    }

    @Override
    public int update(Permission permission) {
        return permissionRepository.update(permission);
    }

    @Override
    public int deleteById(Integer id) {
        return permissionRepository.deleteById(id);
    }
}
