package com.example.coursemanagement.service.impl;

import com.example.coursemanagement.entity.Role;
import com.example.coursemanagement.repository.RoleRepository;
import com.example.coursemanagement.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 角色服务实现类
 */
@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    @Override
    public Optional<Role> findById(Integer id) {
        return roleRepository.findById(id);
    }

    @Override
    public int save(Role role) {
        return roleRepository.save(role);
    }

    @Override
    public int update(Role role) {
        return roleRepository.update(role);
    }

    @Override
    public int deleteById(Integer id) {
        return roleRepository.deleteById(id);
    }
}
